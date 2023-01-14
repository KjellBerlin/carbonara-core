package com.carbonara.core.product

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class ProductQuery(
    private val productService: ProductService
): Query {

    @GraphQLDescription("All available products")
    suspend fun product(): List<Product> {
        return productService.getAllProducts()
    }
}
