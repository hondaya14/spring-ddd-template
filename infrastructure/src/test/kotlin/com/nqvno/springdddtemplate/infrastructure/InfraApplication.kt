package com.nqvno.springdddtemplate.infrastructure

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

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
class InfraApplication

fun main(args: Array<String>) {
    runApplication<InfraApplication>(*args)
}