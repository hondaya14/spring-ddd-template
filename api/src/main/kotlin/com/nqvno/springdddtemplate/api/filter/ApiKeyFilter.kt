package com.nqvno.springdddtemplate.api.filter

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import java.util.*

class ApiKeyFilter : AbstractPreAuthenticatedProcessingFilter() {

    companion object {
        const val API_KEY_HEADER = "X-API-KEY"
    }

    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest?): Any {
        return "N/A"
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest?): Any {
        return Optional.ofNullable(request?.getHeader(API_KEY_HEADER)).orElse("N/A")
    }
}
