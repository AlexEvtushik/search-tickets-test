package ua.searchtickets.direction

import androidx.lifecycle.LifecycleOwner
import com.badoo.mvicore.android.AndroidBindings
import org.koin.dsl.module
import ua.searchtickets.direction.DirectionBindings
import ua.searchtickets.direction.DirectionFragment
import ua.searchtickets.direction.DirectionView

val directionModule = module {
    fun inEventId() = "event_id:to_direction"

    scope<DirectionFragment> {
        scoped { DirectionView() }
        scoped<AndroidBindings<DirectionView>> { (lifecycleOwner: LifecycleOwner) ->
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