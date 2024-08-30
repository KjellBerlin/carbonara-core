package com.carbonara.core.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SlackDeliveryWebhookController(
    private val slackService: SlackService
) {

    @PostMapping("/slack-delivery-status", consumes = ["application/x-www-form-urlencoded"])
    suspend fun handleSlackWebhook(requestBody: SlackWebhookRequestBody): ResponseEntity<Void> {

        val slackPayload = objectMapper.readValue(requestBody.payload, SlackPayload::class.java)
        slackPayload.actions.forEach { action ->
            slackService.handleOrderStatusUpdate(
                orderId = action.value,
                slackOrderStatus = action.action_id)
        }

        return ResponseEntity.ok().build()
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()
    }
}

data class SlackWebhookRequestBody(
    val payload: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlackPayload(
    val actions: List<SlackAction>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlackAction(
    val action_id: String,
    val value: String,
)
