package ru.nk.econav

import com.graphhopper.util.exceptions.PointNotFoundException
import com.graphhopper.util.exceptions.PointOutOfBoundsException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.getKoin
import org.slf4j.event.*
import ru.nk.econav.database.DatabaseFactory
import ru.nk.econav.di.appModule
import ru.nk.econav.routes.geocoding
import ru.nk.econav.routes.routingEngine
import ru.nk.econav.util.ResponseError
import java.time.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = true) {

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    DatabaseFactory.init()
    transaction {
        addLogger(StdOutSqlLogger)
    }

    install(Koin) {
        modules(appModule(this@module))
    }

    install(StatusPages) {

        exception<Exception> {
            call.respond(HttpStatusCode.InternalServerError, ResponseError("Ошибка сервера"))
        }

        exception<IllegalArgumentException> {
            call.respond(HttpStatusCode.BadRequest, ResponseError("Ошибка в использовании API"))
        }

        exception<PointNotFoundException> {
            call.respond(HttpStatusCode.BadRequest, ResponseError("Указанная точка находится вне действия сервиса"))
        }

        exception<PointOutOfBoundsException> {
            call.respond(HttpStatusCode.BadRequest, ResponseError("Указанная точка находится вне действия сервиса"))
        }

    }

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
        })
    }

    routing {
        route("/api") {
            routingEngine(getKoin().get())
            geocoding(getKoin().get())
        }
    }
}
