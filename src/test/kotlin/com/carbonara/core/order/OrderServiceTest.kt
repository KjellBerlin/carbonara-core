package com.carbonara.core.order

import be.woutschoovaerts.mollie.data.payment.PaymentStatus
import com.carbonara.core.address.Address
import com.carbonara.core.order.exception.OrderCreationException
import com.carbonara.core.order.exception.OrderNotFoundException
import com.carbonara.core.order.exception.OrderUpdateException
import com.carbonara.core.payment.InternalPaymentStatus
import com.carbonara.core.payment.PaymentDetails
import com.carbonara.core.payment.MolliePaymentService
import com.carbonara.core.product.ProductDao
import com.carbonara.core.product.ProductService
import com.carbonara.core.slack.SlackMessageService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.OffsetDateTime

class OrderServiceTest {

    private lateinit var orderService: OrderService
    private lateinit var orderRepository: OrderRepository
    private lateinit var productService: ProductService
    private lateinit var molliePaymentService: MolliePaymentService
    private lateinit var slackMessageService: SlackMessageService

    @BeforeEach
    fun init() {
        orderRepository = mockk()
        productService = mockk()
        molliePaymentService = mockk()
        slackMessageService = mockk()
        orderService = OrderService(orderRepository, productService, molliePaymentService, slackMessageService)

        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now() } returns TIME
        every { slackMessageService.sendNewOrderMessage(any()) } returns Unit
    }

    @Nested
    inner class CreateOrderTests {

        @Test
        fun `Happy case`() {
            coEvery { productService.getProductDaosByIds(any()) } returns listOf(TEST_PRODUCT)
            every { orderRepository.save(any()) } returns ORDER_DAO.toMono()
            every { molliePaymentService.createMolliePaymentLink(any(), any(), any()) } returns PAYMENT_DETAILS

            val result = runBlocking { orderService.createOrder(CREATE_ORDER_INPUT) }
            assertEquals(ORDER_DAO.toOrderDto(), result)

            coVerify(exactly = 1) { productService.getProductDaosByIds(listOf(PRODUCT_ID.toString())) }
            coVerify(exactly = 1) {
                molliePaymentService.createMolliePaymentLink(
                    amountInCents = 1000,
                    orderDescription = TEST_PRODUCT.productName,
                    userId = AUTH0_USER_ID
                )
            }
        }

        @Test
        fun `Null return form database`() {
            coEvery { productService.getProductDaosByIds(any()) } returns listOf(TEST_PRODUCT)
            every { orderRepository.save(any()) } returns null.toMono()
            every { molliePaymentService.createMolliePaymentLink(any(), any(), any()) } returns PAYMENT_DETAILS

            assertThrows<OrderCreationException> {
                runBlocking { orderService.createOrder(CREATE_ORDER_INPUT) }
            }

            coVerify(exactly = 1) { productService.getProductDaosByIds(listOf(PRODUCT_ID.toString())) }
            coVerify(exactly = 1) {
                molliePaymentService.createMolliePaymentLink(
                    amountInCents = 1000,
                    orderDescription = TEST_PRODUCT.productName,
                    userId = AUTH0_USER_ID
                )
            }
        }
    }

    @Nested
    inner class HandleOrderPaymentTests {

        @Test
        fun `Happy case`() {
            every { molliePaymentService.getMolliePaymentStatus(any()) } returns PaymentStatus.PAID
            coEvery { orderRepository.findFirstByPaymentId(any()) } returns ORDER_DAO.toMono()
            coEvery { orderRepository.save(any()) } returns ORDER_DAO_PAID.toMono()

            runBlocking { orderService.handleOrderPayment(PAYMENT_ID) }

            verify(exactly = 1) { molliePaymentService.getMolliePaymentStatus(PAYMENT_ID) }
            coVerify(exactly = 1) { orderRepository.findFirstByPaymentId(PAYMENT_ID)}
            coVerify(exactly = 1) { orderRepository.save(ORDER_DAO_PAID) }
        }

        @Test
        fun `Status not paid`() {
            every { molliePaymentService.getMolliePaymentStatus(any()) } returns PaymentStatus.FAILED
            coEvery { orderRepository.findFirstByPaymentId(any()) } returns ORDER_DAO.toMono()
            coEvery { orderRepository.save(any()) } returns ORDER_DAO_PAYMENT_FAILED.toMono()

            runBlocking { orderService.handleOrderPayment(PAYMENT_ID) }

            verify(exactly = 1) { molliePaymentService.getMolliePaymentStatus(PAYMENT_ID) }
            coVerify(exactly = 1) { orderRepository.findFirstByPaymentId(PAYMENT_ID)}
            coVerify(exactly = 1) { orderRepository.save(ORDER_DAO_PAYMENT_FAILED) }
        }

        @Test
        fun `Order already paid`() {
            every { molliePaymentService.getMolliePaymentStatus(any()) } returns PaymentStatus.PAID
            coEvery { orderRepository.findFirstByPaymentId(any()) } returns ORDER_DAO_PAID.toMono()
            coEvery { orderRepository.save(any()) } returns ORDER_DAO_PAID.toMono()

            runBlocking { orderService.handleOrderPayment(PAYMENT_ID) }

            verify(exactly = 1) { molliePaymentService.getMolliePaymentStatus(PAYMENT_ID) }
            coVerify(exactly = 1) { orderRepository.findFirstByPaymentId(PAYMENT_ID)}
            coVerify(exactly = 0) { orderRepository.save(ORDER_DAO_PAID) }
        }

        @Test
        fun `Order can not be found in database`() {
            every { molliePaymentService.getMolliePaymentStatus(any()) } returns PaymentStatus.PAID
            coEvery { orderRepository.findFirstByPaymentId(any()) } returns null.toMono()
            coEvery { orderRepository.save(any()) } returns ORDER_DAO_PAID.toMono()

            assertThrows<OrderNotFoundException> {
                runBlocking { orderService.handleOrderPayment(PAYMENT_ID) }
            }

            verify(exactly = 1) { molliePaymentService.getMolliePaymentStatus(PAYMENT_ID) }
            coVerify(exactly = 1) { orderRepository.findFirstByPaymentId(PAYMENT_ID)}
            coVerify(exactly = 0) { orderRepository.save(ORDER_DAO_PAID) }
        }
    }

    @Nested
    inner class GetNonPendingOrdersByAuth0UserIdTests {

        @Test
        fun `Happy case`() {
            coEvery { orderRepository.findAllByAuth0UserIdAndPaymentStatuses(
                auth0UserId = AUTH0_USER_ID,
                paymentStatuses = listOf(InternalPaymentStatus.PAID.name, InternalPaymentStatus.FAILED.name)
            ) } returns listOf(ORDER_DAO_PAID, ORDER_DAO_PAYMENT_FAILED).toFlux()

            val result = runBlocking { orderService.getNonPendingOrdersByAuth0UserId(AUTH0_USER_ID) }
            assertEquals(listOf(ORDER_DAO_PAID.toOrderDto(), ORDER_DAO_PAYMENT_FAILED.toOrderDto()), result)

            coVerify(exactly = 1) { orderRepository.findAllByAuth0UserIdAndPaymentStatuses(
                auth0UserId = AUTH0_USER_ID,
                paymentStatuses = listOf(InternalPaymentStatus.PAID.name, InternalPaymentStatus.FAILED.name)
            ) }
        }

        @Test
        fun `No orders found`() {
            coEvery { orderRepository.findAllByAuth0UserIdAndPaymentStatuses(
                auth0UserId = AUTH0_USER_ID,
                paymentStatuses = listOf(InternalPaymentStatus.PAID.name, InternalPaymentStatus.FAILED.name)
            ) } returns emptyList<OrderDao>().toFlux()

            val result = runBlocking { orderService.getNonPendingOrdersByAuth0UserId(AUTH0_USER_ID) }
            assertEquals(emptyList<OrderDao>(), result)

            coVerify(exactly = 1) { orderRepository.findAllByAuth0UserIdAndPaymentStatuses(
                auth0UserId = AUTH0_USER_ID,
                paymentStatuses = listOf(InternalPaymentStatus.PAID.name, InternalPaymentStatus.FAILED.name)
            ) }
        }
    }

    @Nested
    inner class UpdateOrderStatusTests {

        @Test
        fun `Happy case - updateOrderStatus`() {
            val deliveredOrder = ORDER_DAO_PAID.copy(orderStatus = OrderStatus.DELIVERED)

            coEvery { orderRepository.findById(ORDER_DAO_PAID.orderId) } returns ORDER_DAO_PAID.toMono()
            coEvery { orderRepository.save(any()) } returns deliveredOrder.toMono()

            val orderDao =
                runBlocking { orderService.updateOrderStatus(ORDER_DAO_PAID.orderId.toString(), OrderStatus.DELIVERED) }

            assertEquals(ORDER_DAO_DELIVERED, orderDao)
            coVerify(exactly = 1) { orderRepository.findById(ORDER_DAO_PAID.orderId) }
            coVerify(exactly = 1) { orderRepository.save(deliveredOrder) }
        }

        @Test
        fun `Order can not be found`() {
            coEvery { orderRepository.findById(ORDER_DAO_PAID.orderId) } returns null.toMono()

            assertThrows<OrderNotFoundException> {
                runBlocking { orderService.updateOrderStatus(ORDER_DAO_PAID.orderId.toString(), OrderStatus.DELIVERED) }
            }

            coVerify(exactly = 1) { orderRepository.findById(ORDER_DAO.orderId) }
            coVerify(exactly = 0) { orderRepository.save(any()) }
        }

        @Test
        fun `Order can not be updated`() {
            val deliveredOrder = ORDER_DAO_PAID.copy(orderStatus = OrderStatus.DELIVERED)

            coEvery { orderRepository.findById(ORDER_DAO_PAID.orderId) } returns ORDER_DAO_PAID.toMono()
            coEvery { orderRepository.save(any()) } returns null.toMono()

            assertThrows<OrderUpdateException> {
                runBlocking { orderService.updateOrderStatus(ORDER_DAO.orderId.toString(), OrderStatus.DELIVERED) }
            }

            coVerify(exactly = 1) { orderRepository.findById(ORDER_DAO.orderId) }
            coVerify(exactly = 1) { orderRepository.save(deliveredOrder) }
        }
    }

    companion object {
        val TIME: OffsetDateTime = OffsetDateTime.parse("2024-06-01T14:00:00.0+02:00")
        private const val AUTH0_USER_ID = "auth0Id1"
        private const val USER_NAME = "Mr Bean"
        val PRODUCT_ID = ObjectId()
        const val PAYMENT_ID = "tr_123"
        val TEST_PRODUCT = ProductDao(
            productId = PRODUCT_ID,
            productName = "test-product-1",
            productPrice = 1000,
            productPictureUrl = "https://example.com",
            isActive = true,
            shortProductDescription = "Short description",
            longProductDescription = "Long description",
            isProductPlaceHolder = false
        )
        val CREATE_ORDER_INPUT = CreateOrderInput(
            auth0UserId = AUTH0_USER_ID,
            userName = USER_NAME,
            deliveryAddress = Address(
                name = "John Watson",
                street = "Baker Street",
                streetNumber = "221B",
                postCode = "123",
                city = "London",
                country = "Germany",
                googlePlaceId = "sample_google_place_id"
            ),
            productsIds = listOf(PRODUCT_ID.toString()),
            additionalDetails = "No additional details"
        )
        val PAYMENT_DETAILS = PaymentDetails(
            paymentId = PAYMENT_ID,
            paymentRedirectLink = "https://example.com",
            internalPaymentStatus = InternalPaymentStatus.PENDING
        )
        val ORDER_DAO = OrderDao(
            orderId = ObjectId(),
            auth0UserId = AUTH0_USER_ID,
            userName = USER_NAME,
            deliveryAddress = CREATE_ORDER_INPUT.deliveryAddress,
            products = listOf(TEST_PRODUCT),
            additionalDetails = CREATE_ORDER_INPUT.additionalDetails,
            paymentDetails = PAYMENT_DETAILS,
            orderStatus = OrderStatus.FINDING_AVAILABLE_RIDER,
            createdAt = TIME.toString(),
            updatedAt = TIME.toString()
        )
        val ORDER_DAO_PAID = ORDER_DAO.copy(
            paymentDetails = ORDER_DAO.paymentDetails.copy(internalPaymentStatus = InternalPaymentStatus.PAID)
        )
        val ORDER_DAO_PAYMENT_FAILED = ORDER_DAO.copy(
            paymentDetails = ORDER_DAO.paymentDetails.copy(
                internalPaymentStatus = InternalPaymentStatus.FAILED),
            orderStatus = OrderStatus.PAYMENT_FAILED
        )
        val ORDER_DAO_DELIVERED = ORDER_DAO_PAID.copy(orderStatus = OrderStatus.DELIVERED)
    }
}
