package com.carbonara.core.product

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class ProductQuery(
    private val productService: ProductService
): Query {

    @GraphQLDescription("All available products")
    suspend fun product(): List<ProductDto> {
        return productService.getAllProducts()
    }

    @GraphQLDescription("The product which is available for sale at the moment")
    suspend fun activeProduct(): ProductDto? {
        return productService.getActiveProduct()
    }
}
