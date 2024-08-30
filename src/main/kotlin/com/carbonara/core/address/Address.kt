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

    override fun toString(): String {
        return "$street $streetNumber, $postCode $city"
    }

    fun createGoogleMapsLink(): String {
        return "https://www.google.com/maps/place/?q=place_id:$googlePlaceId"
    }
}

