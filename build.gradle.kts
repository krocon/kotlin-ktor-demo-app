plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "com.example"
version = "1.0.0"

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Server engine + core
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)

    // JSON via kotlinx.serialization
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Error handling, validation, logging
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.logback.classic)

    // Live streaming: WebSockets + Server-Sent Events
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.sse)

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.client.websockets)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
