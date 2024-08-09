package com.carbonara.core.delivery

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackDeliveryWebhookController(
    private val orderService: OrderService
) {

    // Potential dos attack endpoint, introduce rate limiting

    @PostMapping("/slack-delivery-status", consumes = ["application/json"])
    suspend fun handleSlackWebhook(@RequestParam("payload") payload: String) {
        log.info("--Start Slack--")
        log.info(payload)
        log.info("--End Slack--")

    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
