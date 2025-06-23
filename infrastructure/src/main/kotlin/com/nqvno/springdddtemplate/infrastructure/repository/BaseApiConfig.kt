package com.nqvno.springdddtemplate.infrastructure.repository

import com.nqvno.springdddtemplate.domain.LoggerDelegate
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.http.io.SocketConfig
import org.apache.hc.core5.pool.PoolConcurrencyPolicy
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestClient

/**
 * Base configuration class for API clients.
 * Provides a common configuration for REST clients with connection pooling, logging, and timeout settings.
 */
abstract class BaseApiConfig {
    /**
     * Creates a configured RestClient with the provided HTTP headers and base URL.
     *
     * @param httpHeaders Headers to be added to all requests
     * @param baseUrl Base URL for all requests from this client
     * @return Configured RestClient instance
     */
    fun createRestClient(
        httpHeaders: HttpHeaders,
        baseUrl: String,
    ): RestClient {
        val connectionManager = createConnectionManager()
        val httpClient = createHttpClient(connectionManager)

        return RestClient.builder()
            .requestFactory(HttpComponentsClientHttpRequestFactory(httpClient))
            .baseUrl(baseUrl)
            .applyJacksonConfig()
            .defaultHeaders { it.addAll(httpHeaders) }
            .requestInterceptor(LoggingRequestInterceptor())
            .build()
    }

    /**
     * Creates a connection manager with configured timeouts and pool settings.
     */
    private fun createConnectionManager() = PoolingHttpClientConnectionManagerBuilder.create()
        .setDefaultSocketConfig(createSocketConfig())
        .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
        .setMaxConnTotal(CONNECTION_POOL_MAX_TOTAL)
        .setMaxConnPerRoute(CONNECTION_POOL_MAX_PER_ROUTE)
        .setDefaultConnectionConfig(createConnectionConfig())
        .build()

    /**
     * Creates socket configuration with read timeout.
     */
    private fun createSocketConfig() = SocketConfig.custom()
        .setSoTimeout(Timeout.ofMilliseconds(TIMEOUT_READ_MILLIS))
        .build()

    /**
     * Creates connection configuration with various timeout settings.
     */
    private fun createConnectionConfig() = ConnectionConfig.custom()
        .setConnectTimeout(Timeout.ofMilliseconds(TIMEOUT_CONNECTION_MILLIS))
        .setTimeToLive(TimeValue.ofMinutes(CONNECTION_TTL_MINUTES))
        .setValidateAfterInactivity(TimeValue.ofSeconds(INACTIVITY_VALIDATE_SECONDS))
        .setSocketTimeout(Timeout.ofMilliseconds(TIMEOUT_SOCKET_MILLIS))
        .build()

    /**
     * Creates HTTP client with connection manager and connection management settings.
     */
    private fun createHttpClient(connectionManager: org.apache.hc.client5.http.io.HttpClientConnectionManager) = HttpClients
        .custom()
        .setConnectionManager(connectionManager)
        .setConnectionReuseStrategy { _, _, _ -> false } // Don't reuse connections
        .evictIdleConnections(TimeValue.ofSeconds(IDLE_CONNECTION_EVICT_SECONDS))
        .setKeepAliveStrategy { _, _ -> TimeValue.ofSeconds(KEEP_ALIVE_SECONDS) }
        .build()

    /**
     * Extension function to apply Jackson configuration to RestClient.Builder.
     */
    private fun RestClient.Builder.applyJacksonConfig() = messageConverters { converters ->
        converters.forEach {
            if (it is MappingJackson2HttpMessageConverter) {
                with(it.objectMapper) {
                    configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                }
            }
        }
    }

    /**
     * Interceptor for logging HTTP request and response details.
     */
    private class LoggingRequestInterceptor : ClientHttpRequestInterceptor {
        override fun intercept(
            request: HttpRequest,
            body: ByteArray,
            execution: ClientHttpRequestExecution,
        ): ClientHttpResponse {
            logger.debug("--> {} {}", request.method, request.uri)
            val start = System.currentTimeMillis()
            val response = execution.execute(request, body)
            val elapsed = System.currentTimeMillis() - start
            logger.debug(
                "<-- {} {} {} ({}ms)",
                response.statusCode,
                request.method,
                request.uri,
                elapsed
            )
            return response
        }
    }

    companion object {
        /** Common HTTP headers */
        const val HEADER_NAME_CONTENT_TYPE = "Content-Type"
        const val HEADER_VALUE_CONTENT_TYPE = "application/json"

        /** Timeout settings (milliseconds) */
        private const val TIMEOUT_READ_MILLIS = 10000L
        private const val TIMEOUT_CONNECTION_MILLIS = 10000L
        private const val TIMEOUT_SOCKET_MILLIS = 10000L

        /** Connection pool settings */
        private const val CONNECTION_POOL_MAX_TOTAL = 5000
        private const val CONNECTION_POOL_MAX_PER_ROUTE = 3000

        /** Connection lifecycle settings */
        private const val CONNECTION_TTL_MINUTES = 10L
        private const val INACTIVITY_VALIDATE_SECONDS = 1L
        private const val IDLE_CONNECTION_EVICT_SECONDS = 9L
        private const val KEEP_ALIVE_SECONDS = 10L

        private val logger by LoggerDelegate()
    }
}
