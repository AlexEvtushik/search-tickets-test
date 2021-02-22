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
        val directionFraction: Double = MIN_DIRECTION_FRACTION,
        val leftLoadingTime: Long = MAX_LOADING_TIME
    )

    sealed class Wish {
        object LoadTickets : Wish()
    }

    sealed class Effect {
        class UpdateLoadingTime(val fraction: Double, val loadingTime: Long) : Effect()
    }

    object News

    object Bootstrapper : BaseBootstrapper<Wish> {
        override fun invoke(): Observable<Wish> = Observable.just(Wish.LoadTickets)
    }

    object Actor : BaseActor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> = when (wish) {
            Wish.LoadTickets -> loadTickets(state)
        }
            .observeOn(AndroidSchedulers.mainThread())

        private fun loadTickets(state: State): Observable<Effect> =
            Observable.interval(INIT_DELAY, PERIOD, TimeUnit.MILLISECONDS)
                .takeUntil { tick -> tick * PERIOD == state.leftLoadingTime }
                .map { tick ->
                    val passedTime = tick * PERIOD
                    Effect.UpdateLoadingTime(
                        fraction = passedTime * 100.0 / MAX_LOADING_TIME * MAX_DIRECTION_FRACTION / 100.0,
                        loadingTime = state.leftLoadingTime - passedTime
                    )
                }
    }

    object Reducer : BaseReducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State = when (effect) {
            is Effect.UpdateLoadingTime -> state.copy(
                directionFraction = effect.fraction,
                leftLoadingTime = effect.loadingTime
            )
        }
    }

    private companion object {
        const val INIT_DELAY = 1000L
        const val PERIOD = 25L
        const val MAX_LOADING_TIME = 20_000L
        const val MAX_DIRECTION_FRACTION = 0.99
        const val MIN_DIRECTION_FRACTION = 0.0
    }
}