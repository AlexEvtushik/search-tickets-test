package ua.searchtickets.direction

import androidx.lifecycle.LifecycleOwner
import org.koin.dsl.module

val directionModule = module {
    fun inEventId() = "event_id:to_direction"

    scope<DirectionFragment> {
        scoped { DirectionView() }
        scoped { (lifecycleOwner: LifecycleOwner) ->
            DirectionBindings(
                lifecycleOwner = lifecycleOwner,
                feature = get(),
                sharedFeature = get(),
                inEventId = inEventId(),
                coordinator = get()
            )
        }
    }
}