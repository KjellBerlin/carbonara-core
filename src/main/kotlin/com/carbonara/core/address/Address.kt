package com.carbonara.core.address

data class Address(
    val name: String? = null,
    val street: String?,
    val streetNumber: String? ,
    val postCode: String?,
    val city: String?,
    val googlePlaceId: String?
)
