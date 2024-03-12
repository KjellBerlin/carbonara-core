package com.carbonara.core.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        /*
        This is where we configure the security required for our endpoints and setup our app to serve as
        an OAuth2 Resource Server, using JWT validation.
        */
        return http
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/public").permitAll()
                    .requestMatchers("/api/private").authenticated()
                    .requestMatchers("/api/private-scoped").hasAuthority("SCOPE_create:orders")
            }
            .cors(withDefaults())
            .oauth2ResourceServer { oauth2 ->
                oauth2
                    .jwt(withDefaults())
            }
            .build()
    }
}