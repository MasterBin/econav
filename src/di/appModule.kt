package ru.nk.econav.di

import com.google.maps.GeoApiContext
import io.ktor.application.*
import org.koin.dsl.module
import ru.nk.econav.security.JwtFactory
import ru.nk.econav.serivice.RouteService
import ru.nk.econav.serivice.RoutingManager


fun appModule(application : Application) = module {

    single { application }

    single { JwtFactory("asdasdasd;lfaslkdjf;lasakjd") }

    single {
        GeoApiContext.Builder()
            .apply {
                apiKey(
                    get<Application>().environment.config.property("googleMaps.apiKey").getString()
                )
            }.build()
    }

    single(createdAtStart = true) { RoutingManager() }

    single {
        RouteService(get())
    }
}