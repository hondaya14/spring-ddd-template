package com.nqvno.springdddtemplate.api.handler

import com.nqvno.springdddtemplate.api.generated.model.UnauthenticatedView
import com.nqvno.springdddtemplate.api.model.ResponseCode
import com.nqvno.springdddtemplate.domain.LoggerDelegate
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class UnauthenticatedEntryPoint : AuthenticationEntryPoint {

    companion object {
        private val logger by LoggerDelegate()
        private val RESPONSE_BODY = ObjectMapper().writeValueAsString(
            UnauthenticatedView(code = ResponseCode.UNAUTHENTICATED.name)
        )
    }

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        response?.contentType = MediaType.APPLICATION_JSON_VALUE
        response?.status = HttpStatus.UNAUTHORIZED.value()
        logger.warn("Invalid API User", authException)
        response?.outputStream?.print(RESPONSE_BODY)
    }
}
