package com.carbonara.core.slack

import com.carbonara.core.order.OrderService
import com.carbonara.core.order.OrderStatus
import org.springframework.stereotype.Service

@Service
class SlackService(
    private val orderService: OrderService,
    private val slackMessageService: SlackMessageService
) {

    suspend fun handleOrderStatusUpdate(
        orderId: String,
        slackOrderStatus: String,
        messageTimestamp: String,
        userName: String
    ) {
        val orderStatus = mapSlackOrderStatusToOrderStatus(slackOrderStatus)
        val order = orderService.updateOrderStatus(
            orderId = orderId,
            orderStatus = orderStatus
        )
        slackMessageService.updateOrderMessage(
            SlackMessageParams(
                customerName = order.userName,
                orderId = orderId,
                address = order.deliveryAddress.toString(),
                googleMapsLink = order.deliveryAddress.createGoogleMapsLink(),
                productNames = order.products.map { it.productName },
                timeStamp = messageTimestamp,
                orderStatus = orderStatus,
                userName = userName
            )
        )
    }

    private fun mapSlackOrderStatusToOrderStatus(
        slackOrderStatus: String
    ): OrderStatus {
        return when(slackOrderStatus) {
            "accept" -> OrderStatus.RIDER_ASSIGNED
            "delivery_in_progress" -> OrderStatus.DELIVERY_IN_PROGRESS
            "delivered" -> OrderStatus.DELIVERED
            "cancelled" -> OrderStatus.CANCELLED
            else -> throw IllegalArgumentException("Invalid slack order status: $slackOrderStatus")
        }
    }
}
