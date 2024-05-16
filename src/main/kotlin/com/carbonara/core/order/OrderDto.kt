package com.carbonara.core.order

import com.carbonara.core.product.ProductDto
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID

@GraphQLDescription("A customer order for a foodbox")
data class OrderDto (
    @GraphQLDescription("Unique identifier of an order")
    val orderId: ID,
    @GraphQLDescription("UserId of user who has placed the order")
    val auth0UserId: String,
    @GraphQLDescription("Delivery address of the order")
    val deliveryAddress: Address,
    @GraphQLDescription("Ordered products")
    val productDtos: List<ProductDto>
)
