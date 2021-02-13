package ua.searchtickets.data.repositories

import io.reactivex.Single
import ua.searchtickets.data.datasources.SearchTicketsWebApi
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.domain.repositories.CitiesRepository

class RemoteCitiesRepository(
    private val backend: SearchTicketsWebApi
) : CitiesRepository {
    override fun searchCities(query: String): Single<List<CityEntity>> =
        backend.searchCities(query).map { it.cities }
}