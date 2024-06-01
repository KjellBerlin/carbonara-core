package com.carbonara.core.serviceAvailability

import com.carbonara.core.address.Address
import com.carbonara.core.serviceAvailability.googlePlaces.GoogleMapsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ServiceAvailabilityServiceTest {

    private lateinit var googleMapsService: GoogleMapsService
    private lateinit var serviceAvailabilityService: ServiceAvailabilityService

    @BeforeEach
    fun init() {
        googleMapsService = mockk()
        serviceAvailabilityService = ServiceAvailabilityService(googleMapsService)
    }

    @Test
    fun `Happy case - service available at target address`() {
        every { googleMapsService.getCompleteAddress(any()) } returns ADDRESS
        every { googleMapsService.getDistanceToWarehouse(any()) } returns 5

        val expectedResult = ServiceAvailabilityDto(
            available = true,
            address = ADDRESS
        )

        val result = serviceAvailabilityService.checkServiceAvailability(googlePlaceId = GOOGLE_PLACE_ID)

        assertEquals(expectedResult, result)
        verify(exactly = 1) { googleMapsService.getCompleteAddress(GOOGLE_PLACE_ID)}
        verify(exactly = 1) { googleMapsService.getDistanceToWarehouse(GOOGLE_PLACE_ID) }
    }

    @Test
    fun `Happy case - service unavailable at target address`() {
        every { googleMapsService.getCompleteAddress(any()) } returns ADDRESS
        every { googleMapsService.getDistanceToWarehouse(any()) } returns 2000

        val expectedResult = ServiceAvailabilityDto(
            available = false
        )

        val result = serviceAvailabilityService.checkServiceAvailability(googlePlaceId = GOOGLE_PLACE_ID)

        assertEquals(expectedResult, result)
        verify(exactly = 1) { googleMapsService.getCompleteAddress(GOOGLE_PLACE_ID)}
        verify(exactly = 1) { googleMapsService.getDistanceToWarehouse(GOOGLE_PLACE_ID) }
    }

    @Test
    fun `Address properties not complete`() {
        every { googleMapsService.getCompleteAddress(any()) } returns ADDRESS.copy(streetNumber = null)
        every { googleMapsService.getDistanceToWarehouse(any()) } returns 5

        val expectedResult = ServiceAvailabilityDto(
            available = false,
            addressIncomplete = true,
            address = null
        )

        val result = serviceAvailabilityService.checkServiceAvailability(googlePlaceId = GOOGLE_PLACE_ID)

        assertEquals(expectedResult, result)
        verify(exactly = 1) { googleMapsService.getCompleteAddress(GOOGLE_PLACE_ID)}
        verify(exactly = 1) { googleMapsService.getDistanceToWarehouse(GOOGLE_PLACE_ID) }
    }

    companion object {
        const val GOOGLE_PLACE_ID = "sample_google_place_id"
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
