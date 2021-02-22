package ua.searchtickets.searchtickets

import androidx.lifecycle.LifecycleOwner
import com.badoo.mvicore.android.AndroidTimeCapsule
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import ua.searchtickets.domain.entities.CityEntity

val searchTicketsModule = module {
    scope<SearchTicketsFragment> {
        scoped { SearchTicketsView() }
        scoped { (
                     lifecycleOwner: LifecycleOwner,
                     timeCapsule: AndroidTimeCapsule,
                     directionFrom: CityEntity,
                     directionTo: CityEntity
                 ) ->
            SearchTicketsBindings(
                lifecycleOwner = lifecycleOwner,
                feature = get { parametersOf(timeCapsule, directionFrom, directionTo) }
            )
        }
    }
}