package ua.searchtickets.app.di

import org.koin.dsl.module
import ua.searchtickets.domain.usecases.SearchCitiesUseCase

val useCaseModule = module {
    factory { SearchCitiesUseCase(get()) }
}
