package com.carbonara.core.address

data class Address(
    val name: String? = null,
    val street: String?,
    val streetNumber: String?,
    val postCode: String?,
    val city: String?,
    val country: String?,
    val googlePlaceId: String? = null
) {
    fun addressPropertiesComplete(): Boolean {
        return street != null && streetNumber != null && postCode != null && city != null && country != null
    }
}

