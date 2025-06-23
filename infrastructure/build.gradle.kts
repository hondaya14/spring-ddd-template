import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.protobuf)
}

val mybatisGeneratorConfiguration: Configuration by configurations.creating

dependencies {
    implementation(project(":domain"))

    implementation(platform(libs.spring.boot.bom))
    implementation(platform(rootProject.libs.aws.bom))
    implementation(rootProject.libs.aws.s3)
    implementation(rootProject.libs.aws.sts)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.aop)
    implementation(libs.mybatis.spring.boot.starter)
    implementation(libs.oracle.jdbc.ojdbc11)
    implementation(libs.kotlin.reflect)
    implementation(libs.apache.http.client)
    implementation(libs.spring.retry)
    implementation(libs.protobuf.kotlin)
    implementation(libs.protobuf.java.util)
    annotationProcessor(libs.spring.boot.configuration.processor)
    mybatisGeneratorConfiguration(libs.mybatis.generator.core)
    mybatisGeneratorConfiguration(libs.oracle.jdbc.ojdbc11)
    testImplementation(libs.spring.boot.starter.test)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.asProvider().get()}"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                id("kotlin")
            }
        }
    }
}

tasks.register<JavaExec>("mybatisGenerate") {
    group = "mybatisGenerator"
    classpath = mybatisGeneratorConfiguration
    mainClass.set("org.mybatis.generator.api.ShellRunner")
    args = listOf(
        "-configfile", "$rootDir/infrastructure/src/main/resources/mybatisConfig.xml",
        "-overwrite",
        "-verbose"
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
}
