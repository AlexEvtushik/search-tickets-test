package ua.searchtickets.searchtickets

import androidx.lifecycle.LifecycleOwner
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import ua.searchtickets.domain.entities.CityEntity

val searchTicketsModule = module {
    scope<SearchTicketsFragment> {
        scoped { SearchTicketsView() }
        scoped { (
                     lifecycleOwner: LifecycleOwner,
                     directionFrom: CityEntity,
                     directionTo: CityEntity
                 ) ->
            SearchTicketsBindings(
                lifecycleOwner = lifecycleOwner,
                feature = get { parametersOf(directionFrom, directionTo) }
            )
        }
    }
}