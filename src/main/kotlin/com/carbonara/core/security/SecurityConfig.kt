package com.carbonara.core.security

import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/graphql").authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2
                    .jwt(withDefaults())
            }
            .build()
    }
}
