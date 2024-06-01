package com.carbonara.core.serviceAvailability

import com.carbonara.core.address.Address

data class ServiceAvailabilityDto (
    val available: Boolean,
    val address: Address? = null,
)
