package ua.searchtickets.searchtickets

import com.badoo.mvicore.feature.ActorReducerFeature
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.searchtickets.SearchTicketsFeature.*
import java.util.concurrent.TimeUnit
import com.badoo.mvicore.element.Actor as BaseActor
import com.badoo.mvicore.element.Bootstrapper as BaseBootstrapper
import com.badoo.mvicore.element.Reducer as BaseReducer

class SearchTicketsFeature(initialState: State) :
    ActorReducerFeature<Wish, Effect, State, News>(
        initialState = initialState,
        actor = Actor,
        reducer = Reducer,
        bootstrapper = Bootstrapper
    ) {

    data class State(
        val directionFrom: CityEntity,
        val directionTo: CityEntity,
        val directionFraction: Double = 0.0
    )

    sealed class Wish {
        object LoadTickets : Wish()
    }

    sealed class Effect {
        class UpdateLoadingTime(val fraction: Double) : Effect()
    }

    object News

    object Bootstrapper : BaseBootstrapper<Wish> {
        override fun invoke(): Observable<Wish> = Observable.just(Wish.LoadTickets)
    }

    object Actor : BaseActor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<out Effect> = when (wish) {
            Wish.LoadTickets -> loadTickets()
        }
            .observeOn(AndroidSchedulers.mainThread())

        private fun loadTickets(): Observable<out Effect> =
            Observable.interval(1, TimeUnit.SECONDS)
                .takeUntil { tick -> tick.toInt() == MAX_LOADING_TIME }
                .map {
                    Effect.UpdateLoadingTime(
                        fraction = it.toInt() * 100.0 / MAX_LOADING_TIME
                                * MAX_DIRECTION_FRACTION / 100.0
                    )
                }
    }

    object Reducer : BaseReducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State = when (effect) {
            is Effect.UpdateLoadingTime -> state.copy(
                directionFraction = effect.fraction
            )
        }
    }

    private companion object {
        const val MAX_LOADING_TIME = 20
        const val MAX_DIRECTION_FRACTION = 1.0
    }
}