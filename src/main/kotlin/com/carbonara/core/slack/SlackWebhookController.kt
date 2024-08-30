package com.carbonara.core.slack

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackDeliveryWebhookController(
    private val orderService: OrderService
) {

    // Potential dos attack endpoint, introduce rate limiting

    // TODO: Handle webhook

    @PostMapping("/slack-delivery-status", consumes = ["application/x-www-form-urlencoded"])
    suspend fun handleSlackWebhook(requestBody: SlackWebhookRequestBody) {
        log.info("--Start Slack--")
        log.info(requestBody.payload)
        log.info("--End Slack--")
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

data class SlackWebhookRequestBody(
    val payload: String
)
