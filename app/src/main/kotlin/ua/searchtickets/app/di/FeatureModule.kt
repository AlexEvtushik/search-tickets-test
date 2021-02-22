package ua.searchtickets.app.di

import com.badoo.mvicore.android.AndroidTimeCapsule
import org.koin.dsl.module
import ua.searchtickets.cities.CitiesFeature
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.sharedfeature.SharedFeature
import ua.searchtickets.direction.DirectionFeature
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.searchtickets.SearchTicketsFeature

val featureModule = module {
    single { SharedFeature() }
    factory { (timeCapsule: AndroidTimeCapsule) ->
        DirectionFeature(timeCapsule)
    }
    factory { (
                  timeCapsule: AndroidTimeCapsule,
                  directionType: DirectionType
              ) ->
        CitiesFeature(
            timeCapsule = timeCapsule,
            initialState = CitiesFeature.State(directionType),
            searchCitiesUseCase = get()
        )
    }
    factory { (
                  timeCapsule: AndroidTimeCapsule,
                  directionFrom: CityEntity,
                  directionTo: CityEntity
              ) ->
        SearchTicketsFeature(
            timeCapsule = timeCapsule,
            SearchTicketsFeature.State(
                directionFrom = directionFrom,
                directionTo = directionTo
            )
        )
    }
}
