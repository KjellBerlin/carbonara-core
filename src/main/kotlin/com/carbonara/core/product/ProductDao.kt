package com.carbonara.core.product

import com.expediagroup.graphql.generator.scalars.ID
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("Product")
data class ProductDao(
    @Id
    val productId: ObjectId,
    val productName: String,
    val productPrice: Int,
    val productPictureUrl: String,
    var isActive: Boolean?
) {
    fun toProduct(): Product {
        return Product(
            productId = ID(productId.toString()),
            productName = productName,
            productPrice = productPrice,
            productPictureUrl = productPictureUrl,
            isActive = isActive ?: false
        )
    }
}
