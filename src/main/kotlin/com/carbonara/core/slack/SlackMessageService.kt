package com.carbonara.core.slack

import com.slack.api.Slack
import com.slack.api.methods.kotlin_extension.request.chat.blocks
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SlackMessageService {

    @Value("\${slack.token}")
    lateinit var slackToken: String

    @Value("\${slack.channel}")
    lateinit var slackChannel: String

    fun sendNewOrderMessage(
        customerName: String,
        orderId: String,
        address: String,
        googleMapsLink: String,
        productNames: List<String>
    ) {

        val slack = Slack.getInstance()
        val response = slack.methods(slackToken).chatPostMessage { req -> req
            .channel(slackChannel)
            .blocks {
                section {
                    fields {
                        markdownText("*Customer Name:*\n$customerName")
                        markdownText("*OrderId:*\n$orderId")
                    }
                }
                section {
                    fields {
                        markdownText("*Address:*\n$address\n$googleMapsLink")
                        markdownText("*Products:*\n${productNames.joinToString(", ")}")
                    }
                }
                actions {
                    button {
                        text("ACCEPT", emoji = true)
                        style("primary")
                        value("processing_order")
                        actionId("processing_order")
                    }
                    button {
                        text("DELIVERY IN PROGRESS", emoji = true)
                        style("primary")
                        value("delivery_in_progress")
                        actionId("delivery_in_progress")
                    }
                    button {
                        text("DELIVERED", emoji = true)
                        style("danger")
                        value("delivered")
                        actionId("delivered")
                    }
                    button {
                        text("CANCELLED", emoji = true)
                        style("danger")
                        value("cancelled")
                        actionId("cancelled")
                    }
                }
                divider()
            }
        }

        if (!response.isOk) {
            log.error("Slack API error: ${response.error}")
            throw SlackException("Failed to send slack message for orderId: $orderId. Error: ${response.error}")
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
