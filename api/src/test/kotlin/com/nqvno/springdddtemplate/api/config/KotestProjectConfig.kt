package com.nqvno.springdddtemplate.api.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.allure.AllureTestReporter
import io.kotest.extensions.spring.SpringAutowireConstructorExtension

class KotestProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(
        AllureTestReporter(), // For collecting allure data.
        SpringAutowireConstructorExtension
    )
}
