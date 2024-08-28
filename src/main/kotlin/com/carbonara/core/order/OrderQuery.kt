package com.carbonara.core.order

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class OrderQuery(
    private val orderService: OrderService
): Query {

    @GraphQLDescription("Get all paid orders of an user")
    suspend fun paidOrders(userId: String): List<OrderDto> {
        return orderService.getNonPendingOrdersByAuth0UserId(userId)
    }
}
