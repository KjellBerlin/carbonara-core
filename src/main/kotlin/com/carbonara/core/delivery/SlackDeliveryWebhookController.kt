package com.carbonara.core.delivery

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackDeliveryWebhookController(
    private val orderService: OrderService
) {

    // Potential dos attack endpoint, introduce rate limiting

    @PostMapping("/slack-delivery-status", consumes = ["application/json"])
    suspend fun handleSlackWebhook(@RequestBody requestBody: String) {
        log.info("--Start Slack--")
        log.info(requestBody)
        log.info("--End Slack--")

    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
