package ua.searchtickets.common.sharedfeature.events

import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.sharedfeature.EventId
import ua.searchtickets.common.sharedfeature.SharedEvent
import ua.searchtickets.domain.entities.CityEntity

class CitiesSharedEvent(
    override val eventId: EventId,
    val city: CityEntity,
    val directionType: DirectionType
) : SharedEvent