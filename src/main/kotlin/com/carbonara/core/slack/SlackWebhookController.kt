package com.carbonara.core.slack

import com.carbonara.core.order.OrderService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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

        val slackPayload = objectMapper.readValue(requestBody.payload, SlackPayload::class.java)
        slackPayload.actions.forEach { action ->
            log.info("Action: ${action.action_id}, Value: ${action.value}")
        }

        log.info("--End Slack--")
    }

    companion object {
        private val log = KotlinLogging.logger {}
        private val objectMapper = jacksonObjectMapper()
    }
}

data class SlackWebhookRequestBody(
    val payload: String
)

data class SlackPayload(
    val actions: List<SlackAction>
)

data class SlackAction(
    val action_id: String,
    val value: String
)
