package com.carbonara.core.slack

import com.carbonara.core.order.OrderService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackDeliveryWebhookController(
    private val orderService: OrderService
) {

    @PostMapping("/slack-delivery-status", consumes = ["application/x-www-form-urlencoded"])
    suspend fun handleSlackWebhook(requestBody: SlackWebhookRequestBody) {
        log.info("--Start Slack--")

        requestBody.actions.forEach { action ->
            log.info("Action: ${action.action_id}, Value: ${action.value}")
        }

        log.info("--End Slack--")
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

data class SlackWebhookRequestBody(
    val actions: List<SlackAction>
)

data class SlackAction(
    val action_id: String,
    val value: String
)
