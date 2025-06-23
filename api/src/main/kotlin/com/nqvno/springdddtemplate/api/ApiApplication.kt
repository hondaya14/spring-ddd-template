package com.nqvno.springdddtemplate.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import java.security.Security

@SpringBootApplication(
    scanBasePackages = [
        "com.nqvno.springdddtemplate",
    ],
)
@ConfigurationPropertiesScan(
    basePackages = [
        "com.nqvno.springdddtemplate",
    ],
)
class ApiApplication

fun main(args: Array<String>) {
    // DNSキャッシュを無効化
    Security.setProperty("networkaddress.cache.ttl", "0")
    Security.setProperty("networkaddress.cache.negative.ttl", "0")

    runApplication<ApiApplication>(*args)
}
