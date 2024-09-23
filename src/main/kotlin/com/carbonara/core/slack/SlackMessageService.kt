package com.carbonara.core.slack

import com.carbonara.core.order.OrderStatus
import com.slack.api.Slack
import com.slack.api.methods.kotlin_extension.request.chat.blocks
import com.slack.api.methods.response.chat.ChatUpdateResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SlackMessageService {

    @Value("\${slack.token}")
    private lateinit var slackToken: String

    @Value("\${slack.channel}")
    private lateinit var slackChannel: String

    fun sendNewOrderMessage(
        params: SlackMessageParams
    ) {

        val slack = Slack.getInstance()
        val response = slack.methods(slackToken).chatPostMessage { req -> req
            .channel(slackChannel)
            .blocks {
                section {
                    fields {
                        markdownText("*Customer Name:*\n${params.customerName}")
                        markdownText("*OrderId:*\n${params.orderId}")
                    }
                }
                section {
                    fields {
                        markdownText("*Address:*\n${params.address}\n${params.googleMapsLink}")
                        markdownText("*Products:*\n${params.productNames.joinToString(", ")}")
                    }
                }
                actions {
                    button {
                        text("ACCEPT", emoji = true)
                        style("primary")
                        value(params.orderId)
                        actionId("accept")
                    }
                    button {
                        text("DELIVERY IN PROGRESS", emoji = true)
                        style("primary")
                        value(params.orderId)
                        actionId("delivery_in_progress")
                    }
                    button {
                        text("DELIVERED", emoji = true)
                        style("primary")
                        value(params.orderId)
                        actionId("delivered")
                    }
                    button {
                        text("CANCELLED", emoji = true)
                        style("danger")
                        value(params.orderId)
                        actionId("cancelled")
                    }
                }
                divider()
            }
        }

        if (!response.isOk) {
            log.error("Slack API error: ${response.error}")
            throw SlackException("Failed to send slack message for orderId: ${params.orderId}. Error: ${response.error}")
        }
    }

    fun updateOrderMessage(
        params: SlackMessageParams
    ) {
        val slackResponse = when(params.orderStatus) {
            OrderStatus.RIDER_ASSIGNED -> updateOrderMessageToAccepted(params)
            OrderStatus.DELIVERY_IN_PROGRESS -> updateOrderMessageToDeliveryInProgress(params)
            OrderStatus.DELIVERED -> updateOrderMessageToDelivered(params)
            OrderStatus.CANCELLED -> updateOrderMessageToCancelled(params)
            else -> throw IllegalArgumentException("Cannot update slack message based on order status ${params.orderStatus}")
        }

        if (slackResponse == null) {
            log.error("Slack API error: Slack response null")
            throw SlackException("Failed to update slack message for orderId: ${params.orderId}. Error: Slack response null")
        } else if (!slackResponse.isOk) {
            log.error("Slack API error: ${slackResponse.error}")
            throw SlackException("Failed to update slack message for orderId: ${params.orderId}. Error: ${slackResponse.error}")
        }
    }

    private fun updateOrderMessageToAccepted(
        params: SlackMessageParams
    ): ChatUpdateResponse? {

        val slack = Slack.getInstance()
        return slack.methods(slackToken).chatUpdate { req -> req
            .channel(slackChannel)
            .ts(params.timeStamp)
            .blocks {
                section {
                    fields {
                        markdownText("*Customer Name:*\n${params.customerName}")
                        markdownText("*OrderId:*\n${params.orderId}")
                    }
                }
                section {
                    fields {
                        markdownText("*Address:*\n${params.address}\n${params.googleMapsLink}")
                        markdownText("*Products:*\n${params.productNames.joinToString(", ")}")
                    }
                }
                section {
                    fields {
                        markdownText("*Rider:*\n<@${params.slackUserId}>")
                    }
                }
                actions {
                    button {
                        text("ACCEPT", emoji = true)
                        style("primary")
                        value(params.orderId)
                        actionId("accept")
                    }
                    button {
                        text("DELIVERY IN PROGRESS", emoji = true)
                        value(params.orderId)
                        actionId("delivery_in_progress")
                    }
                    button {
                        text("DELIVERED", emoji = true)
                        value(params.orderId)
                        actionId("delivered")
                    }
                    button {
                        text("CANCELLED", emoji = true)
                        value(params.orderId)
                        actionId("cancelled")
                    }
                }
                divider()
            }
        }
    }

    private fun updateOrderMessageToDeliveryInProgress(
        params: SlackMessageParams
    ): ChatUpdateResponse? {

        val slack = Slack.getInstance()
        return slack.methods(slackToken).chatUpdate { req -> req
            .channel(slackChannel)
            .ts(params.timeStamp)
            .blocks {
                section {
                    fields {
                        markdownText("*Customer Name:*\n${params.customerName}")
                        markdownText("*OrderId:*\n${params.orderId}")
                    }
                }
                section {
                    fields {
                        markdownText("*Address:*\n${params.address}\n${params.googleMapsLink}")
                        markdownText("*Products:*\n${params.productNames.joinToString(", ")}")
                    }
                }
                section {
                    fields {
                        markdownText("*Rider:*\n<@${params.slackUserId}>")
                    }
                }
                actions {
                    button {
                        text("ACCEPT", emoji = true)
                        value(params.orderId)
                        actionId("accept")
                    }
                    button {
                        text("DELIVERY IN PROGRESS", emoji = true)
                        style("primary")
                        value(params.orderId)
                        actionId("delivery_in_progress")
                    }
                    button {
                        text("DELIVERED", emoji = true)
                        value(params.orderId)
                        actionId("delivered")
                    }
                    button {
                        text("CANCELLED", emoji = true)
                        value(params.orderId)
                        actionId("cancelled")
                    }
                }
                divider()
            }
        }
    }

    private fun updateOrderMessageToDelivered(
        params: SlackMessageParams
    ): ChatUpdateResponse? {

        val slack = Slack.getInstance()
        return slack.methods(slackToken).chatUpdate { req -> req
            .channel(slackChannel)
            .ts(params.timeStamp)
            .blocks {
                section {
                    fields {
                        markdownText("*Customer Name:*\n${params.customerName}")
                        markdownText("*OrderId:*\n${params.orderId}")
                    }
                }
                section {
                    fields {
                        markdownText("*Address:*\n${params.address}\n${params.googleMapsLink}")
                        markdownText("*Products:*\n${params.productNames.joinToString(", ")}")
                    }
                }
                section {
                    fields {
                        markdownText("*Rider:*\n<@${params.slackUserId}>")
                    }
                }
                actions {
                    button {
                        text("ACCEPT", emoji = true)
                        value(params.orderId)
                        actionId("accept")
                    }
                    button {
                        text("DELIVERY IN PROGRESS", emoji = true)
                        value(params.orderId)
                        actionId("delivery_in_progress")
                    }
                    button {
                        text("DELIVERED", emoji = true)
                        style("primary")
                        value(params.orderId)
                        actionId("delivered")
                    }
                    button {
                        text("CANCELLED", emoji = true)
                        value(params.orderId)
                        actionId("cancelled")
                    }
                }
                divider()
            }
        }
    }

    private fun updateOrderMessageToCancelled(
        params: SlackMessageParams
    ): ChatUpdateResponse? {

        val slack = Slack.getInstance()
        return slack.methods(slackToken).chatUpdate { req -> req
            .channel(slackChannel)
            .ts(params.timeStamp)
            .blocks {
                section {
                    fields {
                        markdownText("*Customer Name:*\n${params.customerName}")
                        markdownText("*OrderId:*\n${params.orderId}")
                    }
                }
                section {
                    fields {
                        markdownText("*Address:*\n${params.address}\n${params.googleMapsLink}")
                        markdownText("*Products:*\n${params.productNames.joinToString(", ")}")
                    }
                }
                section {
                    fields {
                        markdownText("*Rider:*\n<@${params.slackUserId}>")
                    }
                }
                actions {
                    button {
                        text("ACCEPT", emoji = true)
                        value(params.orderId)
                        actionId("accept")
                    }
                    button {
                        text("DELIVERY IN PROGRESS", emoji = true)
                        value(params.orderId)
                        actionId("delivery_in_progress")
                    }
                    button {
                        text("DELIVERED", emoji = true)
                        value(params.orderId)
                        actionId("delivered")
                    }
                    button {
                        text("CANCELLED", emoji = true)
                        style("danger")
                        value(params.orderId)
                        actionId("cancelled")
                    }
                }
                divider()
            }
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

data class SlackMessageParams(
    val customerName: String,
    val orderId: String,
    val address: String,
    val googleMapsLink: String,
    val productNames: List<String>,
    val timeStamp: String? = null,
    val orderStatus: OrderStatus? = null,
    val slackUserId: String? = null
)
