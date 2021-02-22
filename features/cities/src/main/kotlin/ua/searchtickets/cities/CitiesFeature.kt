package ua.searchtickets.cities

import android.os.Bundle
import android.os.Parcelable
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.mvicore.feature.BaseFeature
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.parcelize.Parcelize
import ua.searchtickets.cities.CitiesFeature.Effect
import ua.searchtickets.cities.CitiesFeature.News
import ua.searchtickets.cities.CitiesFeature.State
import ua.searchtickets.cities.CitiesFeature.Wish
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.rxjava.onError
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.domain.usecases.SearchCitiesUseCase
import com.badoo.mvicore.element.Actor as BaseActor
import com.badoo.mvicore.element.NewsPublisher as BaseNewsPublisher
import com.badoo.mvicore.element.PostProcessor as BasePostProcessor
import com.badoo.mvicore.element.Reducer as BaseReducer

class CitiesFeature(
    timeCapsule: AndroidTimeCapsule,
    initialState: State,
    searchCitiesUseCase: SearchCitiesUseCase
) : BaseFeature<Wish, Wish, Effect, State, News>(
    initialState = timeCapsule.state() ?: initialState,
    wishToAction = { it },
    actor = Actor(searchCitiesUseCase),
    reducer = Reducer,
    newsPublisher = NewsPublisher,
    postProcessor = PostProcessor
) {

    init {
        timeCapsule.register(KEY_TIME_CAPSULE) { state.toParcelable() }
    }

    @Parcelize
    data class State(
        val directionType: DirectionType,
        val cities: List<CityEntity> = emptyList(),
        val query: String? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null
    ) : Parcelable

    sealed class Wish {
        object NavigateBack : Wish()
        class ChangeSearchQuery(val query: String) : Wish()
        class SearchCities(val query: String) : Wish()
        class ChooseCity(val city: CityEntity) : Wish()
    }

    sealed class Effect {
        class SearchQueryChanged(val query: String) : Effect()
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

        private val interruptSignal = PublishRelay.create<Unit>()

        override fun invoke(state: State, wish: Wish): Observable<out Effect> = when (wish) {
            Wish.NavigateBack -> noEffect()
            is Wish.ChooseCity -> noEffect()
            is Wish.ChangeSearchQuery -> changeSearchQuery(wish.query)
            is Wish.SearchCities -> searchCities(wish.query)
        }
            .onError(Effect.ClearError) { error -> Effect.ErrorOccurred(error) }
            .observeOn(AndroidSchedulers.mainThread())

        private fun changeSearchQuery(query: String): Observable<Effect> =
            Observable.just(Effect.SearchQueryChanged(query))

        private fun searchCities(query: String): Observable<Effect> {
            interruptSignal.accept(Unit)
            return searchCitiesUseCase(query)
                .toObservable()
                .takeUntil(interruptSignal)
                .map<Effect> { cities ->
                    Effect.CitiesShown(cities.filter { it.iata.isNotEmpty() })
                }
                .startWith(Effect.LoadingStarted)
        }


        private fun noEffect(): Observable<Effect> = Observable.just(Effect.NoEffect)
    }

    object Reducer : BaseReducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State = when (effect) {
            is Effect.SearchQueryChanged -> state.copy(query = effect.query)
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

    object PostProcessor : BasePostProcessor<Wish, Effect, State> {
        override fun invoke(wish: Wish, effect: Effect, state: State): Wish? = when (effect) {
            is Effect.SearchQueryChanged -> Wish.SearchCities(effect.query)
            else -> null
        }

    }

    private companion object {
        const val KEY_TIME_CAPSULE = "key:cities_time_capsule"
        const val KEY_FEATURE_STATE = "key:cities_feature_state"

        fun AndroidTimeCapsule.state() =
            get<Bundle>(KEY_TIME_CAPSULE)?.getParcelable(KEY_FEATURE_STATE) as? State

        fun State.toParcelable() =
            Bundle().apply { putParcelable(KEY_FEATURE_STATE, this@toParcelable) }
    }
}