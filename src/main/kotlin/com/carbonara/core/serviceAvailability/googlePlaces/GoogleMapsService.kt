package com.carbonara.core.serviceAvailability.googlePlaces

import com.carbonara.core.address.Address
import com.carbonara.core.constants.googleAPIKey
import com.carbonara.core.constants.warehouseGooglePlaceId
import com.carbonara.core.order.OrderService
import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.AddressComponentType
import com.google.maps.model.TravelMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GoogleMapsService {

    private val context: GeoApiContext = GeoApiContext.Builder()
        .apiKey(googleAPIKey)
        .build()

    fun getCompleteAddress(placeId: String): Address {
        return try {
            val response = PlacesApi.placeDetails(context, placeId).await()
            val addressComponents = response.addressComponents.associateBy { it.types.first() }

            Address(
                street = addressComponents[AddressComponentType.ROUTE]?.longName,
                streetNumber = addressComponents[AddressComponentType.STREET_NUMBER]?.longName,
                postCode = addressComponents[AddressComponentType.POSTAL_CODE]?.longName,
                city = addressComponents[AddressComponentType.LOCALITY]?.longName,
                country = addressComponents[AddressComponentType.COUNTRY]?.longName,
                googlePlaceId = response.placeId
            )
        } catch (e: Exception) {
            log.error("Failed to retrieve place details for place ID: $placeId", e)
            throw PlaceDetailsException("Failed to retrieve place details for place ID: $placeId", e)
        }
    }

    fun getDistanceToWarehouse(destinationPlaceId: String): Long {
        return try {
            val request = DistanceMatrixApi.newRequest(context)
                .origins("place_id:$warehouseGooglePlaceId")
                .destinations("place_id:$destinationPlaceId")
                .mode(TravelMode.BICYCLING) // You can change the mode to WALKING, BICYCLING, TRANSIT, etc.
                .await()

            // Extract the distance in meters
            val distanceInMeters = request.rows[0].elements[0].distance.inMeters
            distanceInMeters
        } catch (e: Exception) {
            log.error("Failed to calculate distance between warehouse and place ID $destinationPlaceId", e)
            throw PlaceDetailsException("Failed to calculate distance between warehouse and place ID $destinationPlaceId", e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderService::class.java)
    }
}
