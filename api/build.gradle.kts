plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.openapi.generator)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":query"))
    implementation(project(":infrastructure"))
    testImplementation(project(":testUtil"))
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.tx)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.acuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.micrometer.tracing.bridge.brave)
    implementation(libs.jackson.kotlin)
    implementation(libs.swagger.annotations)
    implementation(libs.logback.json.classic)
    implementation(libs.logback.jackson)
    annotationProcessor(libs.spring.boot.configuration.processor)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.springmockk)
}

val openApiYamlPath = "$projectDir/openapi/openapi.yml"
val generateControllerPackagePath = "com.nqvno.springdddtemplate.api.generated.controller"
val generateViewPackagePath = "com.nqvno.springdddtemplate.api.generated.model"

tasks.register<Delete>("openApiClean") {
    group = "openapi tools"
    val srcPath = "$projectDir/src/main/kotlin"
    val generatedControllerDir = "$srcPath/${generateControllerPackagePath.replace(".", "/")}"
    val generatedViewDir = "$srcPath/${generateViewPackagePath.replace(".", "/")}"
    delete(generatedControllerDir, generatedViewDir)
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set(openApiYamlPath)
    outputDir.set("$projectDir/")
    apiPackage.set(generateControllerPackagePath)
    modelPackage.set(generateViewPackagePath)
    modelNameSuffix.set("view")
    generateApiTests.set(false)
    generateModelTests.set(false)
    typeMappings.set(mapOf("DateTime" to "LocalDateTime"))
    configOptions.set(
        mutableMapOf(
            "skipDefaultInterface" to "true",
            "interfaceOnly" to "true",
            "useSpringBoot3" to "true",
            "gradleBuildFile" to "false",
            "exceptionHandler" to "false",
            "documentationProvider" to "none",
            "useTags" to "true",
        )
    )
}

tasks.openApiGenerate {
    dependsOn("openApiClean")
}

val redoclyConfigPath = "$projectDir/openapi/redocly.yml"
tasks.register<Exec>("redocBuildDocs") {
    group = "openapi tools"
    val outputPath = "$projectDir/build/docs/index.html"
    commandLine("sh", "-c", "redocly build-docs $openApiYamlPath -o $outputPath --config $redoclyConfigPath")
}

tasks.register<Exec>("redocLint") {
    group = "openapi tools"
    commandLine("sh", "-c", "redocly lint $openApiYamlPath  --config $redoclyConfigPath")
}

tasks.withType<Test> {
    useJUnitPlatform()
    dependsOn("dockerComposeUp")
    finalizedBy("dockerComposeDown")
}
