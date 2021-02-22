package ua.searchtickets.app.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ua.searchtickets.cities.CitiesFragment
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.sharedfeature.EventId
import ua.searchtickets.direction.DirectionFragment
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.searchtickets.SearchTicketsFragment

object Screens {
    fun directionScreen() = FragmentScreen { DirectionFragment.newInstance() }

    fun citiesScreen(
        directionType: DirectionType,
        outEventId: EventId
    ) = FragmentScreen { CitiesFragment.newInstance(directionType, outEventId) }

    fun searchTicketsScreen(
        directionFrom: CityEntity,
        directionTo: CityEntity
    ) = FragmentScreen { SearchTicketsFragment.newInstance(directionFrom, directionTo) }
}
