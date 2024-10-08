package com.carbonara.core.product

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductService(
    private val productRepository: ProductRepository,
) {

    suspend fun getAllProducts(): List<ProductDto> {
        return productRepository.findAll().collectList().awaitSingleOrNull()?.map { it.toProductDao() } ?: emptyList()
    }

    suspend fun getActiveProduct(): ProductDto? {
        return productRepository.findFirstByIsActiveIsTrue().awaitSingleOrNull()?.toProductDao()
    }

    suspend fun createProduct(
        createProductInput: CreateProductInput,
    ): ProductDto? {
        val newProduct =  productRepository.save(
            ProductDao(
                productId = ObjectId(),
                productName = createProductInput.productName,
                productPrice = createProductInput.productPrice,
                productPictureUrl = createProductInput.productPictureUrl,
                isActive = false,
                shortProductDescription = createProductInput.shortProductDescription,
                longProductDescription = createProductInput.longProductDescription,
                isProductPlaceHolder = false
            )
        ).awaitSingleOrNull()?.toProductDao()

        log.info("Created new product with ID={}", newProduct?.productId)
        return newProduct
    }

    suspend fun setActiveProduct(
        productId: String,
    ): ProductDto? {
        val activeProducts = productRepository.findAllByIsActiveIsTrue().collectList().awaitSingleOrNull()
        activeProducts?.forEach {
            it.isActive = false
            productRepository.save(it).awaitSingleOrNull()
        }
        val newActiveProduct = productRepository.findById(ObjectId(productId)).awaitSingleOrNull()
            ?: throw Exception("No product found")
        newActiveProduct.isActive = true

        log.info("New active product is the product with productID={}", newActiveProduct.productId)
        return productRepository.save(newActiveProduct).awaitSingleOrNull()?.toProductDao()
            ?: throw Exception("Can not save new active product")
    }

    suspend fun getProductDaosByIds(
        productIds: List<String>
    ): List<ProductDao> {
        val productsIdsIterable = productIds.map { ObjectId(it) }.asIterable()
        val productDaos =  productRepository.findAllById(productsIdsIterable).collectList().awaitSingleOrNull()
        return productDaos ?: emptyList()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductService::class.java)
    }
}
