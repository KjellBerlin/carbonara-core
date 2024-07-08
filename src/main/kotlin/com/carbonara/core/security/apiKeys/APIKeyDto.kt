package com.carbonara.core.security.apiKeys

data class APIKeyDto(
    val googlePlacesAPIKey: String,
    val auth0Domain: String,
    val auth0ClientId: String,
)
