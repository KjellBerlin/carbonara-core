package com.carbonara.core

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class HealthCheck: Query {
    fun health() = "I'm good!"
}
