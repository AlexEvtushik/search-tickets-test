package ua.searchtickets.app.di

import org.koin.dsl.module
import ua.searchtickets.data.repositories.RemoteCitiesRepository
import ua.searchtickets.domain.repositories.CitiesRepository

val repositoryModule = module {
    single<CitiesRepository> { RemoteCitiesRepository(get()) }
}
