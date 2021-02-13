package ua.searchtickets.domain.repositories

import io.reactivex.Single
import ua.searchtickets.domain.entities.CityEntity

interface CitiesRepository {
    fun searchCities(query: String): Single<List<CityEntity>>
}