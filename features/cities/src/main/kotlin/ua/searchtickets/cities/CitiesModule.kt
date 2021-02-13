package ua.searchtickets.cities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.badoo.mvicore.android.AndroidBindings
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.sharedfeature.EventId

val citiesModule = module {
    scope<CitiesFragment> {
        scoped { (activity: AppCompatActivity) ->
            CitiesView(activity)
        }
        scoped<AndroidBindings<CitiesView>> { (
                                                  lifecycleOwner: LifecycleOwner,
                                                  directionType: DirectionType,
                                                  outEventId: EventId
                                              ) ->
            CitiesBindings(
                lifecycleOwner = lifecycleOwner,
                feature = get { parametersOf(directionType) },
                sharedFeature = get(),
                outEventId = outEventId,
                coordinator = get()
            )
        }
    }
}