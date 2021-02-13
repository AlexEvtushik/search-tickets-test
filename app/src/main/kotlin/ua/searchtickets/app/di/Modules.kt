package ua.searchtickets.app.di

import ua.searchtickets.cities.citiesModule
import ua.searchtickets.direction.directionModule
import ua.searchtickets.searchtickets.searchTicketsModule

val modules = listOf(
    citiesModule,
    directionModule,
    featureModule,
    navigationModule,
    networkModule,
    repositoryModule,
    searchTicketsModule,
    useCaseModule
)
