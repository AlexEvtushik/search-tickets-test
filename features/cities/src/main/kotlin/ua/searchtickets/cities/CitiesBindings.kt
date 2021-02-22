package ua.searchtickets.cities

import androidx.lifecycle.LifecycleOwner
import com.badoo.mvicore.android.AndroidBindings
import com.badoo.mvicore.binder.using
import ua.searchtickets.cities.CitiesFeature.News
import ua.searchtickets.cities.CitiesFeature.Wish
import ua.searchtickets.cities.CitiesView.CityItem
import ua.searchtickets.cities.CitiesView.UiEvent
import ua.searchtickets.cities.CitiesView.ViewModel
import ua.searchtickets.common.navigation.Coordinator
import ua.searchtickets.common.sharedfeature.EventId
import ua.searchtickets.common.sharedfeature.SharedFeature
import ua.searchtickets.common.sharedfeature.events.CitiesSharedEvent

class CitiesBindings(
    lifecycleOwner: LifecycleOwner,
    private val feature: CitiesFeature,
    sharedFeature: SharedFeature,
    outEventId: EventId,
    coordinator: Coordinator
) : AndroidBindings<CitiesView>(lifecycleOwner) {

    init {
        binder.bind(feature.news to sharedFeature using { news ->
            when (news) {
                is News.CityChosen -> CitiesSharedEvent(outEventId, news.city, news.directionType)
                else -> null
            }
        })
        binder.bind(feature.news to coordinator using { news ->
            when (news) {
                News.BackClicked -> CitiesNavigationEvent.BackClicked
                is News.CityChosen -> CitiesNavigationEvent.CityChosen
            }
        })
    }

    override fun setup(view: CitiesView) {
        binder.bind(view to feature using { uiEvent ->
            when (uiEvent) {
                UiEvent.BackClicked -> Wish.NavigateBack
                is UiEvent.SearchQueryChanged -> Wish.ChangeSearchQuery(uiEvent.query)
                is UiEvent.CitySelected -> Wish.ChooseCity(uiEvent.city)
            }
        })
        binder.bind(feature to view using { state ->
            ViewModel(
                items = state.cities.map { CityItem(it) },
                query = state.query.orEmpty(),
                isLoading = state.isLoading,
                error = state.error
            )
        })
    }
}