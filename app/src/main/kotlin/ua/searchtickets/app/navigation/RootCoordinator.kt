package ua.searchtickets.app.navigation

import io.reactivex.ObservableSource
import ua.searchtickets.cities.CitiesNavigationEvent
import ua.searchtickets.common.navigation.Coordinator
import ua.searchtickets.common.navigation.NavigationEvent
import ua.searchtickets.direction.DirectionNavigationEvent

class RootCoordinator(private val router: AppRouter) :
    Coordinator,
    ObservableSource<AppScreen> by router {

    sealed class MainNavigationEvent : NavigationEvent {
        object AppStarted : MainNavigationEvent()
    }

    override fun accept(event: NavigationEvent) {
        when (event) {
            MainNavigationEvent.AppStarted -> router.newRootScreen(AppScreen.DirectionScreen)

            is DirectionNavigationEvent.ChooseCityClicked -> router.navigateTo(
                AppScreen.CitiesScreen(event.directionType, event.outEventId)
            )
            is DirectionNavigationEvent.SearchTicketsClicked -> router.navigateTo(
                AppScreen.SearchTicketsScreen(event.directionFrom, event.directionTo)
            )

            CitiesNavigationEvent.BackClicked -> router.exit()
            CitiesNavigationEvent.CityChosen -> router.exit()
        }
    }
}
