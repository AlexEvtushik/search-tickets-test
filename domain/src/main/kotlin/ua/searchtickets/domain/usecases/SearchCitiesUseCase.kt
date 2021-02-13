package ua.searchtickets.domain.usecases

import io.reactivex.Single
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.domain.repositories.CitiesRepository

class SearchCitiesUseCase(private val repository: CitiesRepository) {
    operator fun invoke(query: String): Single<List<CityEntity>> =
        repository.searchCities(query)
}