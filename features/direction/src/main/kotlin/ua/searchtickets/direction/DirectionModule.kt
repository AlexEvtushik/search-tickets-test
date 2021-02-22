package ua.searchtickets.direction

import androidx.lifecycle.LifecycleOwner
import com.badoo.mvicore.android.AndroidTimeCapsule
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val directionModule = module {
    fun inEventId() = "event_id:to_direction"

    scope<DirectionFragment> {
        scoped { DirectionView() }
        scoped { (lifecycleOwner: LifecycleOwner, timeCapsule: AndroidTimeCapsule) ->
            DirectionBindings(
                lifecycleOwner = lifecycleOwner,
                feature = get { parametersOf(timeCapsule) },
                sharedFeature = get(),
                inEventId = inEventId(),
                coordinator = get()
            )
        }
    }
}