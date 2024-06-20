package com.carbonara.core.payment

data class MolliePaymentDetails(
    val paymentRedirectLink: String,
    val paymentId: String,
    val paid: Boolean
)
