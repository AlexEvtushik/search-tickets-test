package com.google.android.gms.maps

import android.content.Context
import android.content.generateBitmapDescriptorWith
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import ua.searchtickets.common.R

fun GoogleMap.addCityMarker(context: Context, position: LatLng, name: String) {
    addMarker(
        MarkerOptions()
            .position(position)
            .icon(
                context.generateBitmapDescriptorWith(
                    name,
                    context.resources.getDimensionPixelSize(R.dimen.marker_text_size).toFloat()
                )
            )
            .alpha(0.9f)
            .anchor(0.5f, 0.5f)
            .zIndex(1F)
    )
}

fun GoogleMap.getPlaneMarker(startPosition: LatLng, endPosition: LatLng): MarkerOptions =
    MarkerOptions()
        .position(startPosition)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_plane))
        .anchor(0.5f, 0.5f)
        .zIndex(2F)
        .rotation(getPlaneMarkerRotation(startPosition, endPosition))

fun GoogleMap.getPlaneMarkerRotation(startPosition: LatLng, endPosition: LatLng): Float =
    SphericalUtil.computeHeading(
        startPosition,
        endPosition
    ).toFloat() - 90f

fun GoogleMap.addPlanePath(startPosition: LatLng, endPosition: LatLng) {
    addPolyline(
        PolylineOptions()
            .addAll(listOf(startPosition, endPosition))
            .geodesic(true)
            .pattern(listOf(Dot(), Gap(10f)))
    )
}