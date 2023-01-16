package com.carbonara.core.product

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
    fun `when creating a product it should be added to the database`() {
        val createProductInput = CreateProductInput(
            productName = "test-product",
            productPrice = 1000
        )

        every {
            productRepository.save(any())
        } returns TEST_PRODUCT.toMono()

        runBlocking {
            productService.createProduct(createProductInput)
        }

        verify (exactly = 1) {
            productRepository.save(
                match {
                    it.productName == TEST_PRODUCT.productName
                            && it.productPrice == TEST_PRODUCT.productPrice
                }
            )
        }
    }

    companion object {
        val TEST_PRODUCT = ProductDto(
            productId = ObjectId(),
            productName = "test-product",
            productPrice = 1000
        )
    }
}
