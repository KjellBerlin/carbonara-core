package com.carbonara.core.payment

data class PaymentDetails(
    val paymentRedirectLink: String,
    val paymentId: String,
    val internalPaymentStatus: InternalPaymentStatus,
)

enum class InternalPaymentStatus {
    PENDING,
    PAID,
    FAILED
}
