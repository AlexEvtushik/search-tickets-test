package ua.searchtickets.common.sharedfeature

import androidx.lifecycle.LifecycleOwner

interface SharedEventOwner {
    fun setSharedEvent(eventId: EventId, event: SharedEvent)
    fun clearSharedEvent(eventId: EventId)

    fun setSharedEventListener(
        eventId: EventId,
        lifecycleOwner: LifecycleOwner,
        listener: SharedEventListener
    )

    fun clearSharedEventListener(eventId: EventId)
}

fun SharedEventOwner.setSharedEventListener(
    eventId: EventId,
    lifecycleOwner: LifecycleOwner,
    listener: (EventId, SharedEvent) -> Unit
) {
    setSharedEventListener(eventId, lifecycleOwner, object : SharedEventListener {
        override fun onEvent(eventId: EventId, event: SharedEvent) {
            listener(eventId, event)
        }
    })
}
