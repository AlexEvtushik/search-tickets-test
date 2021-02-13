package ua.searchtickets.common.sharedfeature

interface SharedEventListener {
    fun onEvent(eventId: EventId, event: SharedEvent)
}