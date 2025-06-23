package com.nqvno.springdddtemplate.api.config

import com.nqvno.springdddtemplate.api.filter.ApiKeyFilter
import com.nqvno.springdddtemplate.api.handler.UnauthenticatedEntryPoint
import com.nqvno.springdddtemplate.api.service.AuthService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun authorizationManager(authorizationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authorizationConfiguration.authenticationManager
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
    ): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/actuator/health", permitAll)
                authorize(anyRequest, authenticated)
            }
            addFilterAt<ApiKeyFilter>(
                ApiKeyFilter().apply {
                    setAuthenticationManager(authenticationManager)
                }
            )
            exceptionHandling {
                authenticationEntryPoint = UnauthenticatedEntryPoint()
            }
        }
        return http.build()
    }

    @Bean
    fun preAuthenticatedAuthenticationProvider(
        authService: AuthService,
    ): PreAuthenticatedAuthenticationProvider? {
        val preAuthenticatedAuthenticationProvider = PreAuthenticatedAuthenticationProvider().apply {
            setPreAuthenticatedUserDetailsService(authService)
            setUserDetailsChecker(AccountStatusUserDetailsChecker())
        }
        return preAuthenticatedAuthenticationProvider
    }
}
