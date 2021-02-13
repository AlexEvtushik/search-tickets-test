package ua.searchtickets.cities

import ua.searchtickets.common.navigation.NavigationEvent

sealed class CitiesNavigationEvent : NavigationEvent {
    object BackClicked : CitiesNavigationEvent()
    object CityChosen : CitiesNavigationEvent()
}
