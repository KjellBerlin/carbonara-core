package com.carbonara.core.slack

import com.slack.api.Slack
import com.slack.api.methods.kotlin_extension.request.chat.blocks
import com.slack.api.model.block.composition.BlockCompositions.plainText
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
                divider()
                section {
                    markdownText("*New Order*")
                }
                /*
                header {
                    plainText("New order")
                }
                section {
                    markdownText("*Customer Name:*\n$customerName\n*OrderId:*\n$orderId")
                }
                section {
                    markdownText("*Address:*\n$address\n$googleMapsLink\n*Products:*\n${productNames.joinToString(", ")}")
                }
                actions {
                    button {
                        text("ACCEPT", emoji = true)
                        style("primary")
                        value("processing_order")
                    }
                    button {
                        text("DELIVERY_IN_PROGRESS", emoji = true)
                        style("primary")
                        value("delivery_in_progress")
                    }
                    button {
                        text("DELIVERED", emoji = true)
                        style("primary")
                        value("delivered")
                    }
                    button {
                        text("CANCELLED", emoji = true)
                        style("danger")
                        value("cancelled")
                    }
                }

                 */
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
