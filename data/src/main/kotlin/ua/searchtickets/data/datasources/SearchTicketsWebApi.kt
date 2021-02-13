package ua.searchtickets.data.datasources

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ua.searchtickets.data.entities.CitiesResponse

interface SearchTicketsWebApi {
    @GET("autocomplete?lang=en")
    fun searchCities(@Query("term") query: String): Single<CitiesResponse>
}