package com.carbonara.core.address

import com.carbonara.core.serviceAvailability.ServiceAvailabilityServiceTest.Companion.GOOGLE_PLACE_ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AddressTest {

    @Test
    fun `Happy case - address complete`() {
        assertEquals(true, ADDRESS.addressPropertiesComplete())
    }

    @Test
    fun `Happy case - address incomplete`() {
        val address = ADDRESS.copy(streetNumber = null)
        assertEquals(false, address.addressPropertiesComplete())
    }

    companion object {
        val ADDRESS = Address(
            street = "Baker Street",
            streetNumber = "221b",
            postCode = "12345",
            city = "London",
            country = "Great Britain",
            googlePlaceId = GOOGLE_PLACE_ID
        )
    }
}
