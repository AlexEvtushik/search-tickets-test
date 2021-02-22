package ua.searchtickets.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationEntity(
    val lat: Double,
    val lon: Double
) : Parcelable