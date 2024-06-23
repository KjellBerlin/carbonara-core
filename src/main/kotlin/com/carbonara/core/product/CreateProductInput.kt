package com.carbonara.core.product

data class CreateProductInput(
    val productName: String,
    val productPrice: Int,
    val productPictureUrl: String
)