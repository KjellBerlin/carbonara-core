package com.carbonara.core.order

data class CreateOrderInput(
    val auth0UserId: String,
    val deliveryAddress: Address,
    val productsIds: List<String>
)
