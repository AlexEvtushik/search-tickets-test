package ua.searchtickets.common.sharedfeature

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.functions.Consumer

class SharedFeature : SharedEventOwner, Consumer<SharedEvent> {
    private val sharedEvents = mutableMapOf<EventId, SharedEvent>()
    private val sharedEventListeners = mutableMapOf<EventId, LifecycleAwareSharedEventListener>()

    override fun accept(t: SharedEvent?) {
        t?.let { event -> setSharedEvent(event.eventId, event) }
    }

    override fun setSharedEvent(eventId: EventId, event: SharedEvent) {
        val resultListener = sharedEventListeners[event.eventId]
        if (resultListener != null && resultListener.isAtLeast(Lifecycle.State.STARTED)) {
            resultListener.onEvent(eventId, event)
        } else {
            sharedEvents[event.eventId] = event
        }
    }

    override fun clearSharedEvent(eventId: EventId) {
        sharedEvents.remove(eventId)
    }

    override fun setSharedEventListener(
        eventId: EventId,
        lifecycleOwner: LifecycleOwner,
        listener: SharedEventListener
    ) {
        val lifecycle = lifecycleOwner.lifecycle
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        val observer: LifecycleEventObserver = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_START) {
                    // once we are started, check for any stored results
                    val sharedEvent = sharedEvents[eventId]
                    if (sharedEvent != null) {
                        // if there is a result, fire the callback
                        listener.onEvent(eventId, sharedEvent)
                        // and clear the result
                        clearSharedEvent(eventId)
                    }
                }
                if (event == Lifecycle.Event.ON_DESTROY) {
                    lifecycle.removeObserver(this)
                    sharedEventListeners.remove(eventId)
                }
            }
        }
        lifecycle.addObserver(observer)
        sharedEventListeners[eventId] = LifecycleAwareSharedEventListener(lifecycle, listener)
    }

    override fun clearSharedEventListener(eventId: EventId) {
        sharedEventListeners.remove(eventId)
    }
}