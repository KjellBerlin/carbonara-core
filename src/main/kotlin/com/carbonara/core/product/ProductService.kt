package com.carbonara.core.product

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class ProductService(
    private val productRepository: ProductRepository
) {

    suspend fun getAllProducts(): List<Product> {
        return productRepository.findAll().collectList().awaitSingleOrNull()?.map { it.toProduct() } ?: emptyList()
    }

    suspend fun createProduct(
        createProductInput: CreateProductInput
    ): Product? {
        return productRepository.save(
            ProductDto(
                productId = ObjectId(),
                productName = createProductInput.productName,
                productPrice = createProductInput.productPrice
            )
        ).awaitSingleOrNull()?.toProduct()
    }
}
