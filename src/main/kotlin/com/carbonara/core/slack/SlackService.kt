package com.carbonara.core.slack

import com.carbonara.core.order.OrderService
import com.carbonara.core.order.OrderStatus
import org.springframework.stereotype.Service

@Service
class SlackService(
    private val orderService: OrderService
) {

    suspend fun handleOrderStatusUpdate(
        orderId: String,
        slackOrderStatus: String
    ) {
        orderService.updateOrderStatus(
            orderId = orderId,
            orderStatus = mapSlackOrderStatusToOrderStatus(slackOrderStatus)
        )
    }

    private fun mapSlackOrderStatusToOrderStatus(
        slackOrderStatus: String
    ): OrderStatus {
        return when(slackOrderStatus) {
            "accept" -> OrderStatus.PROCESSING_ORDER
            "delivery_in_progress" -> OrderStatus.DELIVERY_IN_PROGRESS
            "delivered" -> OrderStatus.DELIVERED
            "cancelled" -> OrderStatus.CANCELLED
            else -> throw IllegalArgumentException("Invalid slack order status")
        }
    }
}
