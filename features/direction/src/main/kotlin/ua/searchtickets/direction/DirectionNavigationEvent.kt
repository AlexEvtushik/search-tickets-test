package ua.searchtickets.direction

import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.navigation.NavigationEvent
import ua.searchtickets.common.sharedfeature.EventId
import ua.searchtickets.domain.entities.CityEntity

sealed class DirectionNavigationEvent : NavigationEvent {
    class ChooseCityClicked(
        val directionType: DirectionType,
        val outEventId: EventId
    ) : DirectionNavigationEvent()
    class SearchTicketsClicked(
        val directionFrom: CityEntity,
        val directionTo: CityEntity
    ) : DirectionNavigationEvent()
}