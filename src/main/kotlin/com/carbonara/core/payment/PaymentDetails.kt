package com.carbonara.core.payment

data class PaymentDetails(
    val paymentRedirectLink: String,
    val paymentId: String,
    val paid: Boolean
)
