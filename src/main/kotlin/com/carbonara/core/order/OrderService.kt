package com.carbonara.core.order

import com.carbonara.core.product.ProductService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService
) {

    suspend fun createOrder(
        createOrderInput: CreateOrderInput
    ): OrderDto? {
        val productDaos = productService.getProductDaosByIds(createOrderInput.productsIds)

        val newOrder = orderRepository.save(
            OrderDao(
                orderId = ObjectId(),
                auth0UserId = createOrderInput.auth0UserId,
                deliveryAddress = createOrderInput.deliveryAddress,
                products = productDaos
            )
        ).awaitSingleOrNull()?.toOrder()

        log.info("Created order with ID={} for user={}", newOrder?.orderId, newOrder?.auth0UserId)
        return newOrder
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderService::class.java)
    }
}
