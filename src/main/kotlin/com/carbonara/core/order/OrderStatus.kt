package com.carbonara.core.order

enum class OrderStatus {
    NONE,
    FINDING_AVAILABLE_RIDER,
    RIDER_ASSIGNED,
    DELIVERY_IN_PROGRESS,
    DELIVERED,
    PAYMENT_FAILED,
    CANCELLED,
}
