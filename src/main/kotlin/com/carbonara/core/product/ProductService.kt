package com.carbonara.core.product

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class ProductService(
    private val productRepository: ProductRepository,
) {

    suspend fun getAllProducts(): List<Product> {
        return productRepository.findAll().collectList().awaitSingleOrNull()?.map { it.toProduct() } ?: emptyList()
    }

    suspend fun getActiveProduct(): Product? {
        return productRepository.findFirstByIsActiveIsTrue().awaitSingleOrNull()?.toProduct()
    }

    suspend fun createProduct(
        createProductInput: CreateProductInput,
    ): Product? {
        val newProduct =  productRepository.save(
            ProductDto(
                productId = ObjectId(),
                productName = createProductInput.productName,
                productPrice = createProductInput.productPrice,
                productPictureUrl = createProductInput.productPictureUrl,
                isActive = false
            )
        ).awaitSingleOrNull()?.toProduct()

        // log.info("Created new product with ID={}", newProduct?.productId)
        return newProduct
    }

    // TODO: add logging

    suspend fun setActiveProduct(
        productId: String,
    ): Product? {
        val activeProducts = productRepository.findAllByIsActiveIsTrue().collectList().awaitSingleOrNull()
        activeProducts?.forEach {
            it.isActive = false
            productRepository.save(it).awaitSingleOrNull()
        }
        val newActiveProduct = productRepository.findById(ObjectId(productId)).awaitSingleOrNull() ?: throw Exception("No product found")
        newActiveProduct.isActive = true
        // log.info("New active product is the product with productID={}", newActiveProduct?.productId)
        return productRepository.save(newActiveProduct).awaitSingleOrNull()?.toProduct() ?: throw Exception("Can not save new active product")
    }
}
