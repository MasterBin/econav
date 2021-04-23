package ru.nk.econav

import database.tables.EcoPlace
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.getKoin
import org.postgis.Point
import org.slf4j.event.*
import ru.nk.econav.database.DatabaseFactory
import ru.nk.econav.di.appModule
import ru.nk.econav.routes.ecoPlaces
import ru.nk.econav.routes.routingEngine
import ru.nk.econav.security.JwtFactory
import java.io.File
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

        route("/api") {
            routingEngine(getKoin().get())
            ecoPlaces(getKoin().get())
        }
    }


//        SchemaUtils.create(EcoPlaceTypes, EcoPlaces)

//        EcoPlaceType.new {
//            this.name = "factory"
//            this.radius = 1f
//        }

//    foo()
}

//private fun foo() {
//    val file = File("factories.json")
//    val text = file.readText()
//
//    transaction {
//        Json {
//            ignoreUnknownKeys = true
//        } .decodeFromString<List<A>>(text).forEach {
//            EcoPlace.new {
//                name = it.fullName
//                address = it.address
//                geometry = Point(it.geoData.coordinates[0], it.geoData.coordinates[1])
//                rating = -1.0
//                type = EcoPlaceType.find { EcoPlaceTypes.name eq "factory" }.first()
//            }
//        }
//    }
//}
//
//@Serializable
//private data class A(
//    @SerialName("Address") val address : String,
//    @SerialName("FullName") val fullName : String,
//    val geoData : Geo
//)
//
//@Serializable
//private data class Geo(
//    val coordinates : List<Double>
//)
//

