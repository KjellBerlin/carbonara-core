package com.carbonara.core.payment

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MolliePaymentWebhookController(
    private val orderService: OrderService
) {

    // Potential dos attack endpoint, introduce rate limiting

    @PostMapping("/mollie-payment-status", consumes = ["application/x-www-form-urlencoded"])
    suspend fun handleMollieWebhook(requestBody: MollieWebhookRequestBody): ResponseEntity<Void> {
        log.info("Webhook received for paymentId: {}", requestBody.id)
        orderService.handleOrderPayment(requestBody.id)
        return ResponseEntity.ok().build()
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

data class MollieWebhookRequestBody(
    val id: String
)
