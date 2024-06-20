package com.carbonara.core.order

import com.carbonara.core.payment.MolliePaymentService
import com.carbonara.core.product.ProductDao
import com.carbonara.core.product.ProductService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

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
                paymentDetails = paymentDetails
            )
        ).awaitSingleOrNull()?.toOrder() ?: run {
            log.error("Failed to save order for user {} to database", createOrderInput.userName)
            throw OrderCreationException("Failed to create order")
        }

        log.info("Created order with ID={} for user={}", newOrder.orderId, newOrder.auth0UserId)
        return newOrder
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

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
