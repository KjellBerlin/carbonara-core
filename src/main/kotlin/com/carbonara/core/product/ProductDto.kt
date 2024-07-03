package com.carbonara.core.product

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID

@GraphQLDescription("A food box for customers")
data class ProductDto (

    @GraphQLDescription("Unique identifier of a product")
    val productId: ID,

    @GraphQLDescription("Name of a product")
    val productName: String,

    @GraphQLDescription("Price of a product in cent")
    val productPrice: Int,

    @GraphQLDescription("Url to the Image of the product")
    val productPictureUrl: String,

    @GraphQLDescription("This product is active right now and can be purchased")
    var isActive: Boolean,

    @GraphQLDescription("A short description for the checkout page")
    var shortProductDescription: String,

    @GraphQLDescription("A long description for the product page")
    var longProductDescription: String
)
