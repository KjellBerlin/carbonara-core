package com.carbonara.core.payment

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MolliePaymentWebhookController(
    private val orderService: OrderService
) {

    // Potential dos attack endpoint, introduce rate limiting
    // TODO: Add response status

    @PostMapping("/mollie-payment-status", "application/x-www-form-urlencoded")
    suspend fun handleMollieWebhook(requestBody: MollieWebhookRequestBody) {
        log.info("Webhook received for paymentId: {}", requestBody.id)
        orderService.handleOrderPayment(requestBody.id)
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

data class MollieWebhookRequestBody(
    val id: String
)
