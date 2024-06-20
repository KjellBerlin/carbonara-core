package com.carbonara.core.payment

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MolliePaymentWebhookController(
    private val orderService: OrderService
) {

    @PostMapping("/mollie-payment-status")
    suspend fun handleMollieWebhook(@RequestParam("id") paymentId: String) {
        log.info("Webhook received for paymentId: {}", paymentId)
        orderService.handleOrderPayment(paymentId)
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
