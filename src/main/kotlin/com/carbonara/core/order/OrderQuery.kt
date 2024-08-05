package com.carbonara.core.order

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class OrderQuery(
    private val orderService: OrderService
): Query {

    @GraphQLDescription("Get all orders for a user")
    suspend fun getOrdersByAuth0UserId(auth0UserId: String): List<OrderDto> {
        return orderService.getOrdersByUserId(auth0UserId)
    }
}
