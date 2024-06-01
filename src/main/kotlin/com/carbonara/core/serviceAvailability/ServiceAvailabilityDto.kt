package com.carbonara.core.serviceAvailability

import com.carbonara.core.address.Address

data class ServiceAvailabilityDto (
    val available: Boolean,
    val addressIncomplete: Boolean = false,
    val address: Address? = null,
)
