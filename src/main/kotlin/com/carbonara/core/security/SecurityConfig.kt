package com.carbonara.core.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/mollie-payment-status").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2
                    .jwt(withDefaults())
            }
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun molliePaymentStatusWebFilter(): WebFilter {
        return WebFilter { exchange, chain ->
            if (exchange.request.uri.path == "/mollie-payment-status") {
                chain.filter(exchange).then(Mono.empty())
            } else {
                chain.filter(exchange)
            }
        }
    }
}
