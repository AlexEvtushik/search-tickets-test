package ua.searchtickets.searchtickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.SphericalUtil
import io.reactivex.rxkotlin.addTo
import ua.searchtickets.common.platform.didSet
import ua.searchtickets.common.vendor.mvicore.MviCoreView

class SearchTicketsView : MviCoreView<Unit, SearchTicketsView.ViewModel>() {

    data class ViewModel(
        val directionFromLocation: LatLng,
        val directionToLocation: LatLng,
        val directionFromName: String,
        val directionToName: String,
        val directionFraction: Double
    )

    private var mapView by didSet<MapView> {
        onCreate(null)

        getMapAsync { googleMap ->
            googleMap.setMaxZoomPreference(MAX_CAMERA_ZOOM)

            states.distinctUntilChanged { state, newState ->
                state.directionFromLocation == newState.directionFromLocation &&
                        state.directionToLocation == newState.directionToLocation &&
                        state.directionFromName == newState.directionFromName &&
                        state.directionToName == newState.directionToName
            }
                .subscribe { state ->
                    googleMap.addCityMarker(
                        context,
                        state.directionFromLocation,
                        state.directionFromName
                    )
                    googleMap.addCityMarker(
                        context,
                        state.directionToLocation,
                        state.directionToName
                    )
                    googleMap.addPlanePath(
                        state.directionFromLocation,
                        state.directionToLocation
                    )

                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            LatLngBounds.builder()
                                .include(state.directionFromLocation)
                                .include(state.directionToLocation)
                                .build(),
                            CAMERA_PADDING
                        )
                    )
                }
                .addTo(disposeBag)

            var planeMarker: Marker? = null
            states.distinctUntilChanged { state, newState ->
                state.directionFromLocation == newState.directionFromLocation &&
                        state.directionToLocation == newState.directionToLocation &&
                        state.directionFraction == newState.directionFraction
            }
                .subscribe { state ->
                    val planeLocation = SphericalUtil.interpolate(
                        state.directionFromLocation,
                        state.directionToLocation,
                        state.directionFraction
                    )
                    if (planeMarker == null) {
                        planeMarker = googleMap.addMarker(
                            googleMap.getPlaneMarker(
                                planeLocation,
                                state.directionToLocation
                            )
                        )
                    } else {
                        planeMarker?.position = planeLocation
                        planeMarker?.rotation = googleMap.getPlaneMarkerRotation(
                            planeLocation,
                            state.directionToLocation
                        )
                    }
                }
                .addTo(disposeBag)
        }
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View =
        inflater.inflate(R.layout.fragment_search_tickets, container, false)

    override fun bindViews(rootView: View) {
        rootView.apply {
            mapView = rootView.findViewById(R.id.mapview_searchtickets)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        mapView.onResume()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mapView.onDestroy()
    }

    private companion object {
        const val MAX_CAMERA_ZOOM = 10f
        const val CAMERA_PADDING = 50
    }
}