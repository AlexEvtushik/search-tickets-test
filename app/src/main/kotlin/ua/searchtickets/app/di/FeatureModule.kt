package ua.searchtickets.app.di

import org.koin.dsl.module
import ua.searchtickets.cities.CitiesFeature
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.sharedfeature.SharedFeature
import ua.searchtickets.direction.DirectionFeature
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.searchtickets.SearchTicketsFeature

val featureModule = module {
    single { SharedFeature() }
    factory { DirectionFeature() }
    factory { (directionType: DirectionType) ->
        CitiesFeature(
            initialState = CitiesFeature.State(directionType),
            searchCitiesUseCase = get()
        )
    }
    factory { (directionFrom: CityEntity, directionTo: CityEntity) ->
        SearchTicketsFeature(
            SearchTicketsFeature.State(
                directionFrom = directionFrom,
                directionTo = directionTo
            )
        )
    }
}
