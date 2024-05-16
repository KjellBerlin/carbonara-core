package com.carbonara.core.order

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import org.springframework.stereotype.Component

@Component
class OrderMutation(
    private val orderService: OrderService
): Mutation {

    @GraphQLDescription("Create a customer order")
    suspend fun createOrder(createOrderInput: CreateOrderInput): OrderDto? {
        return orderService.createOrder(createOrderInput)
    }
}
