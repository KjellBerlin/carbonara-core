package com.carbonara.core.payment

import be.woutschoovaerts.mollie.ClientBuilder
import be.woutschoovaerts.mollie.data.common.Amount
import be.woutschoovaerts.mollie.data.payment.PaymentRequest
import be.woutschoovaerts.mollie.exception.MollieException
import com.carbonara.core.constants.currency
import com.carbonara.core.constants.mollieAPIKey
import com.carbonara.core.constants.paymentWebhookUrl
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Service
class MolliePaymentService {

    private val mollieClient = ClientBuilder()
        .withApiKey(mollieAPIKey)
        .build()

    fun createMolliePaymentLink(amountInCents: Int, orderDescription: String, userId: String): MolliePaymentDetails {
        val amountInEUR = Amount(currency, convertCentsToEuros(amountInCents))
        val paymentRequest = PaymentRequest.builder()
            .amount(amountInEUR)
            .description(orderDescription)
            .redirectUrl("https://example.com")
            .webhookUrl(Optional.of(paymentWebhookUrl))
            .build()

        return try {
            val molliePayment = mollieClient.payments().createPayment(paymentRequest)
            log.info("Created payment for userId={} over amount in cents={} successfully", userId, amountInCents)
            MolliePaymentDetails(
                paymentId = molliePayment.id,
                paymentRedirectLink = molliePayment.links.checkout.href,
                paid = false
            )
        } catch (e: MollieException) {
            e.printStackTrace()
            log.error("Failed to create payment for user={} and amount in cents={}", userId, amountInCents, e)
            throw PaymentCreationException("Failed to create payment")
        }
    }

    fun getMolliePaymentStatus(paymentId: String): Boolean {
        try {
            val molliePayment = mollieClient.payments().getPayment(paymentId)

            /*
            molliePayment.status
            molliePayment.status == PaymentStatus.PAID
             */

        } catch (e: MollieException) {
            e.printStackTrace()
        }
        return true
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
