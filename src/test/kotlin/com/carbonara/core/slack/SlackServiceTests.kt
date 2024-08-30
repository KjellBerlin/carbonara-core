package com.carbonara.core.slack

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

    @BeforeEach
    fun init() {
        orderService = mockk()
        slackService = SlackService(orderService)
    }

    @TestFactory
    fun orderStatusUpdateTests() = listOf(
        OrderStatusUpdateScenario("accept", OrderStatus.PROCESSING_ORDER),
        OrderStatusUpdateScenario("delivery_in_progress", OrderStatus.DELIVERY_IN_PROGRESS),
        OrderStatusUpdateScenario("delivered", OrderStatus.DELIVERED),
        OrderStatusUpdateScenario("cancelled", OrderStatus.CANCELLED)
    ).map { scenario ->
        DynamicTest.dynamicTest("Happy case for order status update with status ${scenario.orderType}") {
            coEvery { orderService.updateOrderStatus(any(), any()) } returns Unit

            runBlocking {
                slackService.handleOrderStatusUpdate("1", scenario.orderType)
            }

            coVerify { orderService.updateOrderStatus("1", scenario.expectedOrderStatus) }
        }
    }
}

data class OrderStatusUpdateScenario(
    val orderType: String,
    val expectedOrderStatus: OrderStatus
)
