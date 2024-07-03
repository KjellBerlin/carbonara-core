package com.carbonara.core.order

import be.woutschoovaerts.mollie.data.payment.PaymentStatus
import com.carbonara.core.address.Address
import com.carbonara.core.payment.PaymentDetails
import com.carbonara.core.payment.MolliePaymentService
import com.carbonara.core.payment.PaymentException
import com.carbonara.core.product.ProductDao
import com.carbonara.core.product.ProductService
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
import reactor.kotlin.core.publisher.toMono
import java.time.OffsetDateTime

class OrderServiceTest {

    private lateinit var orderService: OrderService
    private lateinit var orderRepository: OrderRepository
    private lateinit var productService: ProductService
    private lateinit var molliePaymentService: MolliePaymentService

    @BeforeEach
    fun init() {
        orderRepository = mockk()
        productService = mockk()
        molliePaymentService = mockk()
        orderService = OrderService(orderRepository, productService, molliePaymentService)

        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now() } returns TIME
    }

    @Nested
    inner class CreateOrderTests {

        @Test
        fun `Happy case`() {
            coEvery { productService.getProductDaosByIds(any()) } returns listOf(TEST_PRODUCT)
            every { orderRepository.save(any()) } returns ORDER_DAO.toMono()
            every { molliePaymentService.createMolliePaymentLink(any(), any(), any()) } returns MOLLIE_PAYMENT_DETAILS

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
            every { molliePaymentService.createMolliePaymentLink(any(), any(), any()) } returns MOLLIE_PAYMENT_DETAILS

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
            coEvery { orderRepository.save(any()) } returns ORDER_DAO_PAID.toMono()

            runBlocking { orderService.handleOrderPayment(PAYMENT_ID) }

            verify(exactly = 1) { molliePaymentService.getMolliePaymentStatus(PAYMENT_ID) }
            coVerify(exactly = 0) { orderRepository.findFirstByPaymentId(PAYMENT_ID)}
            coVerify(exactly = 0) { orderRepository.save(ORDER_DAO_PAID) }
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

            assertThrows<PaymentException> {
                runBlocking { orderService.handleOrderPayment(PAYMENT_ID) }
            }

            verify(exactly = 1) { molliePaymentService.getMolliePaymentStatus(PAYMENT_ID) }
            coVerify(exactly = 1) { orderRepository.findFirstByPaymentId(PAYMENT_ID)}
            coVerify(exactly = 0) { orderRepository.save(ORDER_DAO_PAID) }
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
            longProductDescription = "Long description"
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
        val MOLLIE_PAYMENT_DETAILS = PaymentDetails(
            paymentId = PAYMENT_ID,
            paymentRedirectLink = "https://example.com",
            paid = false
        )
        val ORDER_DAO = OrderDao(
            orderId = ObjectId(),
            auth0UserId = AUTH0_USER_ID,
            userName = USER_NAME,
            deliveryAddress = CREATE_ORDER_INPUT.deliveryAddress,
            products = listOf(TEST_PRODUCT),
            additionalDetails = CREATE_ORDER_INPUT.additionalDetails,
            paymentDetails = MOLLIE_PAYMENT_DETAILS,
            createdAt = TIME.toString(),
            updatedAt = TIME.toString()
        )
        val ORDER_DAO_PAID = ORDER_DAO.copy(paymentDetails = ORDER_DAO.paymentDetails.copy(paid = true))
    }
}
