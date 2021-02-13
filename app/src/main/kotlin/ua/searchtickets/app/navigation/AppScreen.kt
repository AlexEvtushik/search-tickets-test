package ua.searchtickets.app.navigation

import ru.terrakok.cicerone.android.support.SupportAppScreen
import ua.searchtickets.cities.CitiesFragment
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.sharedfeature.EventId
import ua.searchtickets.direction.DirectionFragment
import ua.searchtickets.searchtickets.SearchTicketsFragment
import ua.searchtickets.domain.entities.CityEntity

sealed class AppScreen : SupportAppScreen() {
    object DirectionScreen : AppScreen() {
        override fun getFragment() = DirectionFragment.newInstance()
    }
    class CitiesScreen(
        private val directionType: DirectionType,
        private val outEventId: EventId
    ) : AppScreen() {
        override fun getFragment() = CitiesFragment.newInstance(directionType, outEventId)
    }
    class SearchTicketsScreen(
        private val directionFrom: CityEntity,
        private val directionTo: CityEntity
    ) : AppScreen() {
        override fun getFragment() = SearchTicketsFragment.newInstance(directionFrom, directionTo)
    }
}
