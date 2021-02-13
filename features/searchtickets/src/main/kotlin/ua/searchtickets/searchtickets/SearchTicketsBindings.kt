package ua.searchtickets.searchtickets

import androidx.lifecycle.LifecycleOwner
import com.badoo.mvicore.android.AndroidBindings
import com.badoo.mvicore.binder.using
import com.google.android.gms.maps.model.LatLng

class SearchTicketsBindings(
    lifecycleOwner: LifecycleOwner,
    private val feature: SearchTicketsFeature
) : AndroidBindings<SearchTicketsView>(lifecycleOwner) {

    override fun setup(view: SearchTicketsView) {
        binder.bind(feature to view using { state ->
            SearchTicketsView.ViewModel(
                directionFromLocation = LatLng(
                    state.directionFrom.location.lat,
                    state.directionFrom.location.lon
                ),
                directionToLocation = LatLng(
                    state.directionTo.location.lat,
                    state.directionTo.location.lon
                ),
                directionFromName = state.directionFrom.iata.firstOrNull().orEmpty(),
                directionToName = state.directionTo.iata.firstOrNull().orEmpty(),
                directionFraction = state.directionFraction
            )
        })
    }
}