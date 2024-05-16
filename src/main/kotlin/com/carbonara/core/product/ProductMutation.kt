package com.carbonara.core.product

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import org.springframework.stereotype.Component

@Component
class ProductMutation(
    private val productService: ProductService
): Mutation {

    // TODO: needs to be secured to be only accessible by admins
    @GraphQLDescription("Admin controller to create a product")
    suspend fun createProduct(createProductInput: CreateProductInput): ProductDto? {
        return productService.createProduct(createProductInput)
    }

    @GraphQLDescription("Admin controller to set a product to active")
    suspend fun updateProductToActive(productId: String): ProductDto? {
        return productService.setActiveProduct(productId)
    }
}
