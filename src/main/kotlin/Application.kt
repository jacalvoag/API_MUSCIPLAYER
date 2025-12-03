package com.josecalvo

import com.josecalvo.infrastructure.database.DatabaseFactory
import com.josecalvo.infrastructure.web.plugin.configureRouting
import com.josecalvo.infrastructure.web.plugin.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 3000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init(
        driver = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5432/music-player_db",
        user = "postgres",
        password = "ajstyles2006",
        maxPoolSize = 10
    )

    configureRouting()
    configureSerialization()
    configureRouting()
}