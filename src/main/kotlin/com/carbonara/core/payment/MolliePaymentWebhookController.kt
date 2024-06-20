package com.carbonara.core.payment

import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MolliePaymentWebhookController(
    private val molliePaymentService: MolliePaymentService
) {

    @PostMapping("/mollie-payment-status")
    fun handleMollieWebhook(@RequestParam("id") paymentId: String) {
        log.info("Webhook received for paymentId: {}", paymentId)

        // TODO: use orderservice instead of molliepaymentservice service to not introduce circular dependency

        molliePaymentService.getMolliePaymentStatus(paymentId)
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
