package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sse.SSE
import io.ktor.server.websocket.WebSockets

/** Enables the two streaming techniques: WebSockets (two-way) and Server-Sent Events (one-way). */
fun Application.configureSockets() {
    install(WebSockets)
    install(SSE)
}
