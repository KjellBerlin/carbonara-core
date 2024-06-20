package com.carbonara.core.product

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

class ProductServiceTest {

    private lateinit var productService: ProductService
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun init() {
        productRepository = mockk()
        productService = ProductService(productRepository)
    }

    @Test
    fun `when creating a product it should be added to the database and should not be active`() {
        val createProductInput = CreateProductInput(
            productName = TEST_PRODUCT_1_NOT_ACTIVE.productName,
            productPrice = TEST_PRODUCT_1_NOT_ACTIVE.productPrice,
            productPictureUrl = TEST_PRODUCT_1_NOT_ACTIVE.productPictureUrl
        )

        every {
            productRepository.save(any())
        } returns TEST_PRODUCT_1_NOT_ACTIVE.toMono()

        runBlocking {
            productService.createProduct(createProductInput)
        }

        verify (exactly = 1) {
            productRepository.save(
                match {
                    it.productName == TEST_PRODUCT_1_NOT_ACTIVE.productName
                            && it.productPrice == TEST_PRODUCT_1_NOT_ACTIVE.productPrice
                            && it.productPictureUrl == TEST_PRODUCT_1_NOT_ACTIVE.productPictureUrl
                            && it.isActive == TEST_PRODUCT_1_NOT_ACTIVE.isActive
                }
            )
        }
    }

    @Test
    fun `when setting a product to active it should be active`() {
        // setting product 2 active, replacing product 1
        every {
            productRepository.findAllByIsActiveIsTrue()
        } returns listOf(TEST_PRODUCT_1_ACTIVE).toFlux()

        every {
            productRepository.save(TEST_PRODUCT_1_NOT_ACTIVE)
        } returns TEST_PRODUCT_1_NOT_ACTIVE.toMono()

        every {
            productRepository.findById(TEST_PRODUCT_2_NOT_ACTIVE.productId)
        } returns TEST_PRODUCT_2_NOT_ACTIVE.toMono()

        every {
            productRepository.save(TEST_PRODUCT_2_ACTIVE)
        } returns TEST_PRODUCT_2_ACTIVE.toMono()

        runBlocking {
            productService.setActiveProduct(TEST_PRODUCT_2_ACTIVE.productId.toString())
        }

        // product 1 is not active any more
        verify (exactly = 1) {
            productRepository.save(
                match {
                    it.productName == TEST_PRODUCT_1_NOT_ACTIVE.productName
                            && it.productPrice == TEST_PRODUCT_1_NOT_ACTIVE.productPrice
                            && it.productPictureUrl == TEST_PRODUCT_1_NOT_ACTIVE.productPictureUrl
                            && it.isActive == TEST_PRODUCT_1_NOT_ACTIVE.isActive
                }
            )
        }

        // product 2 is active
        verify (exactly = 1) {
            productRepository.save(
                match {
                    it.productName == TEST_PRODUCT_2_ACTIVE.productName
                            && it.productPrice == TEST_PRODUCT_2_ACTIVE.productPrice
                            && it.productPictureUrl == TEST_PRODUCT_2_ACTIVE.productPictureUrl
                            && it.isActive == TEST_PRODUCT_2_ACTIVE.isActive
                }
            )
        }
    }

    @Test
    fun `Happy case - getProductDaosByIds returns one product`() {
        every {
            productRepository.findAllById(listOf(TEST_PRODUCT_1_ACTIVE.productId))
        } returns listOf(TEST_PRODUCT_1_ACTIVE).toFlux()

        val result = runBlocking {
            productService.getProductDaosByIds(listOf(TEST_PRODUCT_1_ACTIVE.productId.toString()))
        }
        assertEquals(listOf(TEST_PRODUCT_1_ACTIVE), result)
    }

    companion object {
        val TEST_PRODUCT_1_NOT_ACTIVE = ProductDao(
            productId = ObjectId(),
            productName = "test-product-1",
            productPrice = 1000,
            productPictureUrl = "https://example.com",
            isActive = false
        )
        val TEST_PRODUCT_1_ACTIVE = ProductDao(
            productId = TEST_PRODUCT_1_NOT_ACTIVE.productId,
            productName = "test-product-1",
            productPrice = 1000,
            productPictureUrl = "https://example.com",
            isActive = true
        )
        val TEST_PRODUCT_2_NOT_ACTIVE = ProductDao(
            productId = ObjectId(),
            productName = "test-product-2",
            productPrice = 1100,
            productPictureUrl = "https://example.com",
            isActive = false
        )
        val TEST_PRODUCT_2_ACTIVE = ProductDao(
            productId = TEST_PRODUCT_2_NOT_ACTIVE.productId,
            productName = "test-product-2",
            productPrice = 1100,
            productPictureUrl = "https://example.com",
            isActive = true
        )
    }
}
