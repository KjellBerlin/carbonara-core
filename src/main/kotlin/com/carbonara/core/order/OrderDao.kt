package com.carbonara.core.order

import com.carbonara.core.address.Address
import com.carbonara.core.payment.PaymentDetails
import com.carbonara.core.product.ProductDao
import com.expediagroup.graphql.generator.scalars.ID
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime

@Document("Order")
data class OrderDao(
    @Id
    val orderId: ObjectId,
    val auth0UserId: String,
    val userName: String,
    val deliveryAddress: Address,
    val products: List<ProductDao>,
    val additionalDetails: String?,
    val paymentDetails: PaymentDetails,
    val orderStatus: OrderStatus,
    val createdAt: String = OffsetDateTime.now().toString(),
    val updatedAt: String = OffsetDateTime.now().toString()
) {

    fun toOrderDto(): OrderDto {
        return OrderDto(
            orderId = ID(orderId.toString()),
            auth0UserId = auth0UserId,
            userName = userName,
            deliveryAddress = deliveryAddress,
            productDtos = products.map { it.toProductDao() },
            additionalDetails = additionalDetails,
            paymentRedirectionLink = paymentDetails.paymentRedirectLink,
            orderStatus = orderStatus
        )
    }
}
