package com.carbonara.core.order

import com.carbonara.core.address.Address
import com.carbonara.core.product.ProductDao
import com.expediagroup.graphql.generator.scalars.ID
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("Order")
data class OrderDao(
    @Id
    val orderId: ObjectId,
    val auth0UserId: String,
    val deliveryAddress: Address,
    val products: List<ProductDao>,
) {

    fun toOrder(): OrderDto {
        return OrderDto(
            orderId = ID(orderId.toString()),
            auth0UserId = auth0UserId,
            deliveryAddress = deliveryAddress,
            productDtos = products.map { it.toProduct() }
        )
    }
}
