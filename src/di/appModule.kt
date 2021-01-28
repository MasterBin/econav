package ru.nk.econav.di

import com.google.maps.GeoApiContext
import io.ktor.application.*
import org.koin.dsl.module
import ru.nk.econav.security.JwtFactory
import ru.nk.econav.serivice.RouteService

/**
 * Created by n.samoylov on 18.11.2020
 */

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

    single {
        RouteService(get())
    }
}