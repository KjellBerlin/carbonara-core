package com.carbonara.core.order

import com.carbonara.core.address.Address
import com.carbonara.core.payment.MolliePaymentDetails
import com.carbonara.core.payment.MolliePaymentService
import com.carbonara.core.product.ProductDao
import com.carbonara.core.product.ProductService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.kotlin.core.publisher.toMono

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
    }

    @Test
    fun `Happy case - createOrder`() {
        coEvery { productService.getProductDaosByIds(any()) } returns listOf(TEST_PRODUCT)
        every { orderRepository.save(any()) } returns ORDER_DAO.toMono()
        every { molliePaymentService.createMolliePaymentLink(any(), any(), any()) } returns MOLLILE_PAYMENT_DETAILS

        val result = runBlocking { orderService.createOrder(CREATE_ORDER_INPUT) }
        assertEquals(ORDER_DAO.toOrder(), result)

        coVerify(exactly = 1) { productService.getProductDaosByIds(listOf(PRODUCT_ID.toString())) }
        coVerify(exactly = 1) { molliePaymentService.createMolliePaymentLink(
            amountInCents = 1000,
            orderDescription = TEST_PRODUCT.productName,
            userId = AUTH0_USER_ID
        ) }
    }

    @Test
    fun `CreateOrder - null return form database`() {
        coEvery { productService.getProductDaosByIds(any()) } returns listOf(TEST_PRODUCT)
        every { orderRepository.save(any()) } returns null.toMono()
        every { molliePaymentService.createMolliePaymentLink(any(), any(), any()) } returns MOLLILE_PAYMENT_DETAILS

        assertThrows<OrderCreationException> {
            runBlocking { orderService.createOrder(CREATE_ORDER_INPUT) }
        }

        coVerify(exactly = 1) { productService.getProductDaosByIds(listOf(PRODUCT_ID.toString())) }
        coVerify(exactly = 1) { molliePaymentService.createMolliePaymentLink(
            amountInCents = 1000,
            orderDescription = TEST_PRODUCT.productName,
            userId = AUTH0_USER_ID
        ) }
    }

    companion object {
        private const val AUTH0_USER_ID = "auth0Id1"
        private const val USER_NAME = "Mr Bean"
        val PRODUCT_ID = ObjectId()
        val TEST_PRODUCT = ProductDao(
            productId = PRODUCT_ID,
            productName = "test-product-1",
            productPrice = 1000,
            productPictureUrl = "https://example.com",
            isActive = true
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
        val MOLLILE_PAYMENT_DETAILS = MolliePaymentDetails(
            paymentId = "tr_123",
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
            paymentDetails = MOLLILE_PAYMENT_DETAILS
        )
    }
}
