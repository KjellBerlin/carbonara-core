package com.carbonara.core.serviceAvailability

import com.carbonara.core.constants.deliveryRadiusInMeters
import com.carbonara.core.serviceAvailability.googlePlaces.GoogleMapsService
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class ServiceAvailabilityService(
    private val googleMapsService: GoogleMapsService
) {

    fun checkServiceAvailability(googlePlaceId: String): ServiceAvailabilityDto = runBlocking {
        val address = async { googleMapsService.getCompleteAddress(googlePlaceId) }.await()
        val distanceInMeters = async { googleMapsService.getDistanceToWarehouse(googlePlaceId) }.await()

        when {
            !address.addressPropertiesComplete() -> {
                ServiceAvailabilityDto(
                    available = false,
                    addressIncomplete = true
                )
            }
            distanceInMeters <= deliveryRadiusInMeters -> {
                ServiceAvailabilityDto(
                    available = true,
                    address = address
                )
            }
            else -> {
                ServiceAvailabilityDto(
                    available = false
                )
            }
        }
    }

}
