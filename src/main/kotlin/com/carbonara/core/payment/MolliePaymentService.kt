package com.carbonara.core.payment

import be.woutschoovaerts.mollie.ClientBuilder
import be.woutschoovaerts.mollie.data.common.Amount
import be.woutschoovaerts.mollie.data.payment.PaymentRequest
import be.woutschoovaerts.mollie.data.payment.PaymentStatus
import be.woutschoovaerts.mollie.exception.MollieException
import com.carbonara.core.constants.currency
import com.carbonara.core.constants.mollieAPIKey
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Service
class MolliePaymentService {

    @Value("\${mollie.redirectUrl}")
    lateinit var redirectUrl: String

    @Value("\${mollie.paymentWebhookUrl}")
    lateinit var paymentWebhookUrl: String

    private val mollieClient = ClientBuilder()
        .withApiKey(mollieAPIKey)
        .build()

    fun createMolliePaymentLink(amountInCents: Int, orderDescription: String, userId: String): PaymentDetails {
        val amountInEUR = Amount(currency, convertCentsToEuros(amountInCents))
        val paymentRequest = PaymentRequest.builder()
            .amount(amountInEUR)
            .description(orderDescription)
            .redirectUrl(redirectUrl)
            .webhookUrl(Optional.of(paymentWebhookUrl))
            .build()

        return try {
            val molliePayment = mollieClient.payments().createPayment(paymentRequest)
            log.info("Created payment for userId={} over amount in cents={} successfully", userId, amountInCents)
            PaymentDetails(
                paymentId = molliePayment.id,
                paymentRedirectLink = molliePayment.links.checkout.href,
                paid = false
            )
        } catch (ex: MollieException) {
            log.error("Failed to create payment for user={} and amount in cents={}, details={}", userId, amountInCents, ex.details)
            throw PaymentException("Failed to create payment")
        }
    }

    fun getMolliePaymentStatus(paymentId: String): PaymentStatus {
        return try {
            val molliePayment = mollieClient.payments().getPayment(paymentId)
            molliePayment.status
        } catch (ex: MollieException) {
            log.error("Failed to retrieve status from mollie for paymentId={}", paymentId, ex)
            throw PaymentException("Failed to retrieve payment status")
        }
    }

    private fun convertCentsToEuros(cents: Int): BigDecimal {
        val centsBigDecimal = BigDecimal.valueOf(cents.toLong())
        val eurosBigDecimal = centsBigDecimal.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        return eurosBigDecimal
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
