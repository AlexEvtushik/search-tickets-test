package ua.searchtickets.direction

import androidx.lifecycle.LifecycleOwner
import com.badoo.mvicore.android.AndroidBindings
import com.badoo.mvicore.binder.using
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.navigation.Coordinator
import ua.searchtickets.common.sharedfeature.EventId
import ua.searchtickets.common.sharedfeature.SharedFeature
import ua.searchtickets.common.sharedfeature.events.CitiesSharedEvent
import ua.searchtickets.common.sharedfeature.setSharedEventListener
import ua.searchtickets.direction.DirectionFeature.News
import ua.searchtickets.direction.DirectionFeature.Wish
import ua.searchtickets.direction.DirectionView.UiEvent
import ua.searchtickets.direction.DirectionView.ViewModel

class DirectionBindings(
    lifecycleOwner: LifecycleOwner,
    private val feature: DirectionFeature,
    sharedFeature: SharedFeature,
    inEventId: EventId,
    coordinator: Coordinator
) : AndroidBindings<DirectionView>(lifecycleOwner) {

    init {
        sharedFeature.setSharedEventListener(inEventId, lifecycleOwner) { _, event ->
            when (event) {
                is CitiesSharedEvent -> Wish.ChangeDirection(event.city, event.directionType)
                else -> null
            }
                ?.let { feature.accept(it) }
        }
        binder.bind(feature.news to coordinator using { news ->
            when (news) {
                News.DirectionFromClicked -> DirectionNavigationEvent.ChooseCityClicked(
                    DirectionType.From,
                    inEventId
                )
                News.DirectionToClicked -> DirectionNavigationEvent.ChooseCityClicked(
                    DirectionType.To,
                    inEventId
                )
                is News.SearchTicketsClicked -> DirectionNavigationEvent.SearchTicketsClicked(
                    news.directionFrom,
                    news.directionTo
                )
            }
        })
    }

    override fun setup(view: DirectionView) {
        binder.bind(view to feature using { uiEvent ->
            when (uiEvent) {
                UiEvent.DirectionFromClicked -> Wish.ChooseDirectionFrom
                UiEvent.DirectionToClicked -> Wish.ChooseDirectionTo
                UiEvent.SearchTicketsClicked -> Wish.SearchTickets
            }
        })
        binder.bind(feature to view using { state ->
            ViewModel(
                directionFrom = state.directionFrom,
                directionTo = state.directionTo,
                error = state.error
            )
        })
    }
}