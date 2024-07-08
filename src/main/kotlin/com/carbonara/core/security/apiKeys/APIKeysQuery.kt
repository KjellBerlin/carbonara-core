package com.carbonara.core.security.apiKeys

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class APIKeysQuery : Query {

    @Value("\${google.apiKey}")
    lateinit var googlePlacesAPIKey: String

    @Value("\${okta.oauth2.issuer}")
    lateinit var auth0Domain: String

    @Value("\${okta.oauth2.client-id}")
    lateinit var auth0ClientId: String

    @GraphQLDescription("APIKeys a user needs to use third party APIs")
    suspend fun apiKeys(): APIKeyDto {
        return APIKeyDto(
            googlePlacesAPIKey = googlePlacesAPIKey,
            auth0Domain = auth0Domain,
            auth0ClientId = auth0ClientId
        )
    }
}
