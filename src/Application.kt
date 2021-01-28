package ru.nk.econav

import com.fasterxml.jackson.databind.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.getKoin
import org.slf4j.event.*
import ru.nk.econav.di.appModule
import ru.nk.econav.routes.location
import ru.nk.econav.security.JwtFactory
import java.time.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = true) {

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(Koin) {
        modules(appModule(this@module))
    }

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(Authentication) {
        jwt {
            verifier(getKoin().get<JwtFactory>().verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("key").asString())
            }
        }
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
        })
    }

    routing {
        webSocket("/myws/echo") {
            send(Frame.Text("Hi from server"))
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    send(Frame.Text("Client said: " + frame.readText()))
                }
            }
        }

        location(getKoin().get())
    }

//    DatabaseFactory.init()
//    transaction {
//        addLogger(StdOutSqlLogger)
//        SchemaUtils.create(Users)
//    }
}

