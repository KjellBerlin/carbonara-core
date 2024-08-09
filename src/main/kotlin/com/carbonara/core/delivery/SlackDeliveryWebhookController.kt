package com.carbonara.core.delivery

import com.carbonara.core.order.OrderService
import com.fasterxml.jackson.databind.JsonNode
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackDeliveryWebhookController(
    private val orderService: OrderService
) {

    // Potential dos attack endpoint, introduce rate limiting

    @PostMapping("/slack-delivery-status", "application/x-www-form-urlencoded")
    suspend fun handleMollieWebhook(requestBody: JsonNode) {
        log.info("--Start Slack--")
        log.info(requestBody.toString())
        log.info("--End Slack--")

    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
