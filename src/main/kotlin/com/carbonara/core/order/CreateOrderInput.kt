package com.carbonara.core.order

import com.carbonara.core.address.Address

data class CreateOrderInput(
    val auth0UserId: String,
    val userName: String,
    val deliveryAddress: Address,
    val productsIds: List<String>,
    val additionalDetails: String? = null
)
