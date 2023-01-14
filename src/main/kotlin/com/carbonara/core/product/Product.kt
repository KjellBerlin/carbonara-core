package com.carbonara.core.product

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@GraphQLDescription("A food box for customers")
class Product (

    @GraphQLDescription("Unique identifier of a product")
    val productId: ID,

    @GraphQLDescription("Name of a product")
    val productName: String,

    @GraphQLDescription("Price of a product in cent")
    val productPrice: Int
)

@Document("Product")
data class ProductDto(
    @Id
    val productId: ObjectId,
    val productName: String,
    val productPrice: Int
) {
    fun toProduct(): Product {
        return Product(
            productId = ID(productId.toString()),
            productName = productName,
            productPrice = productPrice
        )
    }
}
