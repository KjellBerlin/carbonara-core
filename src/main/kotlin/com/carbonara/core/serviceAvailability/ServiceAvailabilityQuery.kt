package com.carbonara.core.serviceAvailability

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class ServiceAvailabilityQuery(
    private val serviceAvailabilityService: ServiceAvailabilityService
): Query {

    @GraphQLDescription("Check if service is available at the requested address and time")
    suspend fun serviceAvailability(googlePlaceId: String): ServiceAvailabilityDto {

        return serviceAvailabilityService.checkServiceAvailability(googlePlaceId)
    }
}
