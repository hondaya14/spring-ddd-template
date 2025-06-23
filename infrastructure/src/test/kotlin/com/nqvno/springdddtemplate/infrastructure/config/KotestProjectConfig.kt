package com.nqvno.springdddtemplate.infrastructure.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.allure.AllureTestReporter
import io.kotest.extensions.spring.SpringAutowireConstructorExtension

class KotestProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(
        SpringAutowireConstructorExtension,
        AllureTestReporter()
    )
}
