package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import org.slf4j.event.Level

/** Logs every incoming request (method, path, status). */
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
}
