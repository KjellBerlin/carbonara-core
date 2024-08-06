package com.carbonara.core.order

import be.woutschoovaerts.mollie.data.payment.PaymentStatus
import com.carbonara.core.payment.MolliePaymentService
import com.carbonara.core.payment.PaymentException
import com.carbonara.core.product.ProductDao
import com.carbonara.core.product.ProductService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
    private val molliePaymentService: MolliePaymentService
) {

    suspend fun createOrder(
        createOrderInput: CreateOrderInput
    ): OrderDto? {
        val productDaos = productService.getProductDaosByIds(createOrderInput.productsIds)

        val paymentDetails = molliePaymentService.createMolliePaymentLink(
            amountInCents = calculateTotalPrice(productDaos),
            orderDescription =  createPaymentDescription(productDaos),
            userId = createOrderInput.auth0UserId
        )

        val newOrder = orderRepository.save(
            OrderDao(
                orderId = ObjectId(),
                auth0UserId = createOrderInput.auth0UserId,
                userName = createOrderInput.userName,
                deliveryAddress = createOrderInput.deliveryAddress,
                products = productDaos,
                additionalDetails = createOrderInput.additionalDetails,
                paymentDetails = paymentDetails,
                orderStatus = OrderStatus.PROCESSING_ORDER
            )
        ).awaitSingleOrNull()?.toOrderDto() ?: run {
            log.error("Failed to save order for user {} to database", createOrderInput.userName)
            throw OrderCreationException("Failed to create order")
        }

        log.info("Created order with ID={} for user={}", newOrder.orderId, newOrder.auth0UserId)
        return newOrder
    }

    suspend fun handleOrderPayment(
        paymentId: String
    ) {
        val paymentStatus = molliePaymentService.getMolliePaymentStatus(paymentId)
        if (paymentStatus == PaymentStatus.PAID) {
            val order = retrieveOrderFromDatabase(paymentId)

            if (!order.paymentDetails.paid) {
                updateOrderToPaid(order, paymentStatus)

                // TODO: trigger delivery
            }
        } else {
            log.info("Retrieved payment status={} for paymentId={}. Not processing order for now further",
                paymentStatus, paymentId)
        }
    }

    suspend fun getPaidOrdersByAuth0UserId(
        auth0UserId: String
    ): List<OrderDto> {
        return orderRepository.findAllByAuth0UserIdAndPaid(auth0UserId)
            .collectList().awaitSingleOrNull()?.map { it.toOrderDto() } ?: emptyList()
    }

    private fun createPaymentDescription(products: List<ProductDao>): String {
        return if (products.size == 1) {
            products.first().productName
        } else {
            products.first().productName + " and more"
        }
    }

    private fun calculateTotalPrice(products: List<ProductDao>): Int {
        return products.sumOf { it.productPrice }
    }

    private suspend fun retrieveOrderFromDatabase(paymentId: String): OrderDao {
        return orderRepository.findFirstByPaymentId(paymentId).awaitSingleOrNull() ?: run {
            log.error("Failed to retrieve order for paymentId={}", paymentId)
            throw PaymentException("Failed to retrieve order for payment")
        }
    }

    private suspend fun updateOrderToPaid(order: OrderDao, paymentStatus: PaymentStatus) {
        log.info("Retrieved payment status={} for orderId={}, now processing order", paymentStatus, order.orderId)
        val updatedOrder = order.copy(
            paymentDetails = order.paymentDetails.copy(paid = true),
            updatedAt = OffsetDateTime.now().toString()
        )
        orderRepository.save(updatedOrder).awaitSingleOrNull() ?: run {
            log.error("Failed to update payment status to paid for orderId={}", order.orderId)
            throw PaymentException("Failed to update payment status")
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
