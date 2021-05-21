package ru.nk.econav.di

import com.google.maps.GeoApiContext
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import org.koin.dsl.module
import ru.nk.econav.repository.EcoPlacesRepository
import ru.nk.econav.repository.GeocodingRepository
import ru.nk.econav.repository.impl.EcoPlacesRepositoryImpl
import ru.nk.econav.repository.impl.GeocodingRepositoryImpl
import ru.nk.econav.serivice.GeocodingService
import ru.nk.econav.serivice.RouteService
import ru.nk.econav.serivice.RoutingManager


fun appModule(application : Application) = module {

    single { application }

    single {
        GeoApiContext.Builder()
            .apply {
                apiKey(
                    get<Application>().environment.config.property("googleMaps.apiKey").getString()
                )
            }.build()
    }

    single(createdAtStart = true) { RoutingManager(get()) }

    single {
        RouteService(get())
    }

    single<EcoPlacesRepository> {
        EcoPlacesRepositoryImpl()
    }

    single {
        GeocodingService(get())
    }

    single<GeocodingRepository> {
        val conf = HoconApplicationConfig(ConfigFactory.load())

        GeocodingRepositoryImpl(conf.property("mapbox.accessToken").getString())
    }
}