package com.carbonara.core.slack

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackDeliveryWebhookController(
    private val orderService: OrderService
) {

    @PostMapping("/slack-delivery-status", consumes = ["application/x-www-form-urlencoded"])
    suspend fun handleSlackWebhook(requestBody: SlackWebhookRequestBody): ResponseEntity<Void> {
        log.info("--Start Slack--")

        log.info(requestBody.payload)

        log.info("--End Slack--")
        return ResponseEntity.ok().build()
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

data class SlackWebhookRequestBody(
    val payload: String
)
