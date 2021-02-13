package ua.searchtickets.cities

import com.badoo.mvicore.feature.ActorReducerFeature
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import ua.searchtickets.cities.CitiesFeature.*
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.rxjava.onError
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.domain.usecases.SearchCitiesUseCase
import com.badoo.mvicore.element.Actor as BaseActor
import com.badoo.mvicore.element.NewsPublisher as BaseNewsPublisher
import com.badoo.mvicore.element.Reducer as BaseReducer

class CitiesFeature(
    initialState: State,
    searchCitiesUseCase: SearchCitiesUseCase
) : ActorReducerFeature<Wish, Effect, State, News>(
    initialState = initialState,
    actor = Actor(searchCitiesUseCase),
    reducer = Reducer,
    newsPublisher = NewsPublisher
) {

    data class State(
        val directionType: DirectionType,
        val cities: List<CityEntity> = emptyList(),
        val query: String? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    sealed class Wish {
        object NavigateBack : Wish()
        class SearchCities(val query: String) : Wish()
        class ChooseCity(val city: CityEntity) : Wish()
    }

    sealed class Effect {
        class CitiesShown(val cities: List<CityEntity>) : Effect()
        object NoEffect : Effect()
        object LoadingStarted : Effect()
        class ErrorOccurred(val error: Throwable) : Effect()
        object ClearError : Effect()
    }

    sealed class News {
        object BackClicked : News()
        class CityChosen(val city: CityEntity, val directionType: DirectionType) : News()
    }

    class Actor(
        private val searchCitiesUseCase: SearchCitiesUseCase
    ) : BaseActor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<out Effect> = when (wish) {
            Wish.NavigateBack -> noEffect()
            is Wish.ChooseCity -> noEffect()
            is Wish.SearchCities -> searchCities(wish.query)
        }
            .onError(Effect.ClearError) { error -> Effect.ErrorOccurred(error) }
            .observeOn(AndroidSchedulers.mainThread())

        private fun searchCities(query: String): Observable<Effect> =
            searchCitiesUseCase(query)
                .toObservable()
                .map<Effect> { cities ->
                    Effect.CitiesShown(cities.filter { it.iata.isNotEmpty() })
                }
                .startWith(Effect.LoadingStarted)

        private fun noEffect(): Observable<Effect> = Observable.just(Effect.NoEffect)
    }

    object Reducer : BaseReducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State = when (effect) {
            is Effect.CitiesShown -> state.copy(cities = effect.cities, isLoading = false)
            Effect.NoEffect -> state
            Effect.LoadingStarted -> state.copy(isLoading = true)
            Effect.ClearError -> state.copy(error = null)
            is Effect.ErrorOccurred -> state.copy(error = effect.error, isLoading = false)
        }
    }

    object NewsPublisher : BaseNewsPublisher<Wish, Effect, State, News> {
        override fun invoke(wish: Wish, effect: Effect, state: State): News? = when (wish) {
            Wish.NavigateBack -> News.BackClicked
            is Wish.ChooseCity -> News.CityChosen(wish.city, state.directionType)
            else -> null
        }
    }
}