package com.carbonara.core.order

import com.carbonara.core.product.ProductDao
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("Order")
data class OrderDao(
    @Id
    val orderId: ObjectId,
    val products: List<ProductDao>
)
