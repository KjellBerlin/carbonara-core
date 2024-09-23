package com.carbonara.core.slack

import com.carbonara.core.helper.createOrderDao
import com.carbonara.core.order.OrderService
import com.carbonara.core.order.OrderStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class SlackServiceTests {

    private lateinit var orderService: OrderService
    private lateinit var slackService: SlackService
    private lateinit var slackMessageService: SlackMessageService

    @BeforeEach
    fun init() {
        orderService = mockk()
        slackMessageService = mockk()
        slackService = SlackService(orderService, slackMessageService)
    }

    @TestFactory
    fun orderStatusUpdateTests() = listOf(
        OrderStatusUpdateScenario("accept", OrderStatus.RIDER_ASSIGNED),
        OrderStatusUpdateScenario("delivery_in_progress", OrderStatus.DELIVERY_IN_PROGRESS),
        OrderStatusUpdateScenario("delivered", OrderStatus.DELIVERED),
        OrderStatusUpdateScenario("cancelled", OrderStatus.CANCELLED)
    ).map { scenario ->
        DynamicTest.dynamicTest("Happy case for order status update with status ${scenario.orderStatus}") {
            val orderDao = createOrderDao(orderStatus = scenario.expectedOrderStatus)
            val slackMessageParams = SlackMessageParams(
                customerName = orderDao.userName,
                orderId = orderDao.orderId.toString(),
                address = orderDao.deliveryAddress.toString(),
                googleMapsLink = orderDao.deliveryAddress.createGoogleMapsLink(),
                productNames = orderDao.products.map { it.productName },
                orderStatus = scenario.expectedOrderStatus,
                timeStamp = "1726842841",
                slackUserId = "sherlock.holmes"
            )

            coEvery { orderService.updateOrderStatus(any(), any()) } returns orderDao
            coEvery { slackMessageService.updateOrderMessage(any()) } returns Unit

            runBlocking {
                slackService.handleOrderStatusUpdate(
                    orderId = orderDao.orderId.toString(),
                    slackOrderStatus = scenario.orderStatus,
                    messageTimestamp = "1726842841",
                    slackUserId = "sherlock.holmes"
                )
            }

            coVerify { orderService.updateOrderStatus(orderDao.orderId.toString(), scenario.expectedOrderStatus) }
            coVerify { slackMessageService.updateOrderMessage(slackMessageParams) }
        }
    }
}

data class OrderStatusUpdateScenario(
    val orderStatus: String,
    val expectedOrderStatus: OrderStatus
)
