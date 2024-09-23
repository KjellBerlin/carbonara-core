package com.carbonara.core.address

import com.carbonara.core.serviceAvailability.ServiceAvailabilityServiceTest.Companion.GOOGLE_PLACE_ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AddressTest {

    @Nested
    inner class AddressPropertiesCompleteTests {

        @Test
        fun `Happy case - address complete`() {
            assertEquals(true, ADDRESS.addressPropertiesComplete())
        }

        @Test
        fun `Happy case - address incomplete`() {
            val address = ADDRESS.copy(streetNumber = null)
            assertEquals(false, address.addressPropertiesComplete())
        }
    }

    @Test
    fun `Happy case - address to string`() {
        val result = ADDRESS.toString()
        assertEquals("Baker Street 221b, 12345 London", result)
    }

    @Test
    fun `Happy case - googleMapsLink`() {
        val result = ADDRESS.createGoogleMapsLink()
        assertEquals("https://www.google.com/maps/search/?api=1&query=%20&query_place_id=$GOOGLE_PLACE_ID", result)
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
