package ua.searchtickets.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityEntity(
    val id: String,
    val country: String,
    val city: String,
    val fullname: String,
    val iata: List<String>,
    val location: LocationEntity
) : Parcelable