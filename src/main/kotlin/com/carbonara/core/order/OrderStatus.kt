package com.carbonara.core.order

enum class OrderStatus {
    NONE,
    PROCESSING_ORDER,
    FINDING_AVAILABLE_RIDER,
    DELIVERY_IN_PROGRESS,
    DELIVERED,
    CANCELLED
}
