package com.carbonara.core.order

import be.woutschoovaerts.mollie.data.payment.PaymentStatus
import com.carbonara.core.payment.InternalPaymentStatus
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
                orderStatus = OrderStatus.NONE
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
        when(val paymentStatus = molliePaymentService.getMolliePaymentStatus(paymentId)) {
            PaymentStatus.PAID -> handlePaidOrder(paymentId, paymentStatus)
            PaymentStatus.CANCELED -> handleUnpaidOrder(paymentId, paymentStatus)
            PaymentStatus.FAILED -> handleUnpaidOrder(paymentId, paymentStatus)
            else -> log.info("Retrieved payment status={} for paymentId={}. Not processing order for now further",
                paymentStatus, paymentId)
        }
    }

    suspend fun getNonPendingOrdersByAuth0UserId(
        auth0UserId: String
    ): List<OrderDto> {
        return orderRepository.findAllByAuth0UserIdAndPaymentStatuses(
            auth0UserId = auth0UserId,
            paymentStatuses = listOf(InternalPaymentStatus.PAID.name, InternalPaymentStatus.FAILED.name)
        ).collectList().awaitSingleOrNull()?.map { it.toOrderDto() } ?: emptyList()
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

    private suspend fun handlePaidOrder(paymentId: String, paymentStatus: PaymentStatus) {
        val order = retrieveOrderFromDatabase(paymentId)

        if (order.paymentDetails.internalPaymentStatus != InternalPaymentStatus.PAID) {
            updateOrderToPaid(order, paymentStatus)

            // TODO: trigger delivery
        }
    }

    private suspend fun handleUnpaidOrder(paymentId: String, paymentStatus: PaymentStatus) {
        val order = retrieveOrderFromDatabase(paymentId)
        updateOrderToPaymentFailed(order, paymentStatus)
    }

    private suspend fun updateOrderToPaid(order: OrderDao, paymentStatus: PaymentStatus) {
        log.info("Retrieved payment status={} for orderId={}, now processing order", paymentStatus, order.orderId)
        val updatedOrder = order.copy(
            paymentDetails = order.paymentDetails.copy(internalPaymentStatus = InternalPaymentStatus.PAID),
            updatedAt = OffsetDateTime.now().toString(),
            orderStatus = OrderStatus.PROCESSING_ORDER
        )
        orderRepository.save(updatedOrder).awaitSingleOrNull() ?: run {
            log.error("Failed to update payment status to paid for orderId={}", order.orderId)
            throw PaymentException("Failed to update payment status")
        }
    }

    private suspend fun updateOrderToPaymentFailed(order: OrderDao, paymentStatus: PaymentStatus) {
        log.info("Retrieved payment status={} for orderId={}, now processing order", paymentStatus, order.orderId)
        val updatedOrder = order.copy(
            paymentDetails = order.paymentDetails.copy(internalPaymentStatus = InternalPaymentStatus.FAILED),
            updatedAt = OffsetDateTime.now().toString(),
            orderStatus = OrderStatus.PAYMENT_FAILED
        )
        orderRepository.save(updatedOrder).awaitSingleOrNull() ?: run {
            log.error("Failed to update payment status to failed for orderId={}", order.orderId)
            throw PaymentException("Failed to update payment status")
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
