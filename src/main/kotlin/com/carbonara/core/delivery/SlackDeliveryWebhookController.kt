package com.carbonara.core.delivery

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackDeliveryWebhookController(
    private val orderService: OrderService
) {

    // Potential dos attack endpoint, introduce rate limiting

    @PostMapping("/slack-delivery-status", "application/x-www-form-urlencoded")
    suspend fun handleMollieWebhook(requestBody: String) {
        log.info("--Start Slack--")
        log.info(requestBody)
        log.info("--End Slack--")

    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
