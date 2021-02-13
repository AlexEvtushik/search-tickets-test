package ua.searchtickets.common.sharedfeature

import androidx.lifecycle.Lifecycle

class LifecycleAwareSharedEventListener(
    private val lifecycle: Lifecycle,
    private val listener: SharedEventListener
) : SharedEventListener {
  fun isAtLeast(state: Lifecycle.State): Boolean {
    return lifecycle.currentState.isAtLeast(state)
  }

  override fun onEvent(eventId: EventId, event: SharedEvent) {
    listener.onEvent(eventId, event)
  }
}