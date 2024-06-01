package com.carbonara.core.order

import com.carbonara.core.address.Address
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
import reactor.kotlin.core.publisher.toMono

class OrderDtoServiceTest {

    private lateinit var orderService: OrderService
    private lateinit var orderRepository: OrderRepository
    private lateinit var productService: ProductService

    @BeforeEach
    fun init() {
        orderRepository = mockk()
        productService = mockk()
        orderService = OrderService(orderRepository, productService)
    }

    @Test
    fun `Happy case - createOrder`() {
        coEvery { productService.getProductDaosByIds(any()) } returns listOf(TEST_PRODUCT)
        every { orderRepository.save(any()) } returns ORDER_DAO.toMono()

        val result = runBlocking { orderService.createOrder(CREATE_ORDER_INPUT) }
        assertEquals(ORDER_DAO.toOrder(), result)

        coVerify(exactly = 1) { productService.getProductDaosByIds(listOf(PRODUCT_ID.toString())) }
    }

    companion object {
        val AUTH0_USER_ID = "auth0Id1"
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
            deliveryAddress = Address(
                name = "John Watson",
                street = "Baker Street",
                streetNumber = "221B",
                postCode = "123",
                city = "London",
                googlePlaceId = "sample_google_place_id"
            ),
            productsIds = listOf(PRODUCT_ID.toString())
        )
        val ORDER_DAO = OrderDao(
            orderId = ObjectId(),
            auth0UserId = AUTH0_USER_ID,
            deliveryAddress = CREATE_ORDER_INPUT.deliveryAddress,
            products = listOf(TEST_PRODUCT)
        )
    }
}
