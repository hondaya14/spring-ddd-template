plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.logback.json.classic)
    implementation(libs.logback.jackson)
    implementation(libs.newrelic.agent.api)
    implementation(project(":domain"))
    implementation(project(":infrastructure"))
    testImplementation(project(":testUtil"))
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
    dependsOn("dockerComposeUp")
    finalizedBy("dockerComposeDown")
}
