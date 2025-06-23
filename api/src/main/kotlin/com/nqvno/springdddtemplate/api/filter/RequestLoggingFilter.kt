package com.nqvno.springdddtemplate.api.filter

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.filter.AbstractRequestLoggingFilter

@Component
class RequestLoggingFilter : AbstractRequestLoggingFilter() {

    companion object {
        const val MAX_PAYLOAD_LENGTH = 120000
        const val BEFORE_LOG_PREFIX = "Incoming request (before): "
        const val AFTER_LOG_PREFIX = "Incoming request  (after): "
    }

    init {
        this.setIncludeQueryString(true)
        this.setIncludeHeaders(true)
        this.setIncludePayload(true)
        this.setIncludeClientInfo(true)
        this.setMaxPayloadLength(MAX_PAYLOAD_LENGTH)
        this.setBeforeMessagePrefix(BEFORE_LOG_PREFIX)
        this.setAfterMessagePrefix(AFTER_LOG_PREFIX)
        // api keyのmasking
        this.setHeaderPredicate { header -> !header.equals(ApiKeyFilter.API_KEY_HEADER, ignoreCase = true) }
    }

    override fun shouldLog(request: HttpServletRequest): Boolean {
        val isHealthCheckRequest = request.requestURL.contains("/actuator/health")
        return logger.isInfoEnabled and !isHealthCheckRequest
    }

    override fun beforeRequest(request: HttpServletRequest, message: String) {
        logger.info(message)
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {
        logger.info(message)
    }
}
