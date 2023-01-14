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
    suspend fun createProduct(createProductInput: CreateProductInput): Product? {
        return productService.createProduct(createProductInput)
    }
}

data class CreateProductInput(
    val productName: String,
    val productPrice: Int
)
