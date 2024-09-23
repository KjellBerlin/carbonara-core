package com.carbonara.core.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
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
        log.info("Received slack webhook for orderId=${slackPayload.actions.first().value} " +
                "and userName=${slackPayload.user.username}")

        slackPayload.actions.forEach { action ->
            slackService.handleOrderStatusUpdate(
                orderId = action.value,
                slackOrderStatus = action.action_id,
                messageTimestamp = slackPayload.message.ts,
                slackUserId = slackPayload.user.id
            )
        }

        return ResponseEntity.ok().build()
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()
        private val log = KotlinLogging.logger {}
    }
}

data class SlackWebhookRequestBody(
    val payload: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlackPayload(
    val actions: List<SlackAction>,
    val message: SlackMessage,
    val user: SlackUser // Not sure if this is here
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlackAction(
    val action_id: String, // Storing orderId in here
    val value: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlackMessage(
    val ts: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlackUser(
    val id: String,
    val username: String
)
