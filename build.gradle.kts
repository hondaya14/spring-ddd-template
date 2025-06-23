import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/** Root projects **/
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.detekt)
    alias(libs.plugins.allure.aggregate.report)
    alias(libs.plugins.allure.adapter)
    alias(libs.plugins.kover)
}

dependencies {
    kover(project(":domain"))
    kover(project(":infrastructure"))
    kover(project(":api"))
    kover(project(":batch"))
}

allure {
    adapter {
        autoconfigure.set(false)
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.generated.*"
                )
            }
        }
    }
}

/** All projects **/
allprojects {
    repositories {
        mavenCentral()
    }
}

/** Sub projects **/
subprojects {
    group = "com.nqvno.springdddtemplate"

    val libs = rootProject.libs

    apply(plugin = libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = libs.plugins.detekt.get().pluginId)
    apply(plugin = libs.plugins.allure.aggregate.report.get().pluginId)
    apply(plugin = libs.plugins.allure.adapter.get().pluginId)
    apply(plugin = libs.plugins.kover.get().pluginId)

    dependencies {
        detektPlugins(libs.detekt.formatting)

        testImplementation(libs.bundles.test)
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = libs.versions.java.get()
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    detekt {
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true
        config.setFrom(file("${rootProject.projectDir}/config/detekt/detekt.yml"))
    }

    tasks.withType<Detekt>().configureEach {
        group = "detekt"
        exclude("**/generated/**")
    }
}
