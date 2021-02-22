package ua.searchtickets.app.navigation

import com.github.terrakok.cicerone.Router
import ua.searchtickets.cities.CitiesNavigationEvent
import ua.searchtickets.common.navigation.Coordinator
import ua.searchtickets.common.navigation.NavigationEvent
import ua.searchtickets.direction.DirectionNavigationEvent

class RootCoordinator(private val router: Router) : Coordinator {

    sealed class MainNavigationEvent : NavigationEvent {
        object ShowDirection : MainNavigationEvent()
    }

    override fun accept(event: NavigationEvent) {
        when (event) {
            MainNavigationEvent.ShowDirection -> router.newRootScreen(Screens.directionScreen())

            is DirectionNavigationEvent.ChooseCityClicked -> router.navigateTo(
                Screens.citiesScreen(event.directionType, event.outEventId)
            )
            is DirectionNavigationEvent.SearchTicketsClicked -> router.navigateTo(
                Screens.searchTicketsScreen(event.directionFrom, event.directionTo)
            )

            CitiesNavigationEvent.BackClicked -> router.exit()
            CitiesNavigationEvent.CityChosen -> router.exit()
        }
    }
}
