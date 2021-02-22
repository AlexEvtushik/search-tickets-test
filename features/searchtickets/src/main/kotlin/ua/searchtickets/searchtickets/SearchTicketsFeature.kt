package ua.searchtickets.searchtickets

import android.os.Bundle
import android.os.Parcelable
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.mvicore.feature.ActorReducerFeature
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.parcelize.Parcelize
import ua.searchtickets.domain.entities.CityEntity
import ua.searchtickets.searchtickets.SearchTicketsFeature.Effect
import ua.searchtickets.searchtickets.SearchTicketsFeature.News
import ua.searchtickets.searchtickets.SearchTicketsFeature.State
import ua.searchtickets.searchtickets.SearchTicketsFeature.Wish
import java.util.concurrent.TimeUnit
import com.badoo.mvicore.element.Actor as BaseActor
import com.badoo.mvicore.element.Bootstrapper as BaseBootstrapper
import com.badoo.mvicore.element.Reducer as BaseReducer

class SearchTicketsFeature(
    timeCapsule: AndroidTimeCapsule,
    initialState: State
) :
    ActorReducerFeature<Wish, Effect, State, News>(
        initialState = timeCapsule.state() ?: initialState,
        actor = Actor,
        reducer = Reducer,
        bootstrapper = Bootstrapper
    ) {

    init {
        timeCapsule.register(KEY_TIME_CAPSULE) { state.toParcelable() }
    }

    @Parcelize
    data class State(
        val directionFrom: CityEntity,
        val directionTo: CityEntity,
        val directionFraction: Double = MIN_DIRECTION_FRACTION,
        val passedTime: Long = 0
    ) : Parcelable

    sealed class Wish {
        object LoadTickets : Wish()
    }

    sealed class Effect {
        class UpdateLoadingTime(val fraction: Double, val passedTime: Long) : Effect()
    }

    object News

    object Bootstrapper : BaseBootstrapper<Wish> {
        override fun invoke(): Observable<Wish> = Observable.just(Wish.LoadTickets)
    }

    object Actor : BaseActor<State, Wish, Effect> {

        private val interruptSignal = PublishRelay.create<Unit>()

        override fun invoke(state: State, wish: Wish): Observable<Effect> = when (wish) {
            Wish.LoadTickets -> loadTickets(state.passedTime)
        }
            .observeOn(AndroidSchedulers.mainThread())

        private fun loadTickets(passedTime: Long): Observable<Effect> {
            interruptSignal.accept(Unit)
            return Observable.interval(INIT_DELAY, PERIOD, TimeUnit.MILLISECONDS)
                .takeWhile { tick -> tick * PERIOD + passedTime < MAX_LOADING_TIME }
                .takeUntil(interruptSignal)
                .map { tick ->
                    val newPassedTime = tick * PERIOD + passedTime
                    Effect.UpdateLoadingTime(
                        fraction = newPassedTime * 100.0 / MAX_LOADING_TIME
                                * MAX_DIRECTION_FRACTION / 100.0,
                        passedTime = newPassedTime
                    )
                }
        }
    }

    object Reducer : BaseReducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State = when (effect) {
            is Effect.UpdateLoadingTime -> state.copy(
                directionFraction = effect.fraction,
                passedTime = effect.passedTime
            )
        }
    }

    private companion object {
        const val INIT_DELAY = 1000L
        const val PERIOD = 25L
        const val MAX_LOADING_TIME = 20_000L
        const val MAX_DIRECTION_FRACTION = 1
        const val MIN_DIRECTION_FRACTION = 0.0

        const val KEY_TIME_CAPSULE = "key:search_tickets_time_capsule"
        const val KEY_FEATURE_STATE = "key:search_tickets_feature_state"

        fun AndroidTimeCapsule.state() =
            get<Bundle>(KEY_TIME_CAPSULE)?.getParcelable(KEY_FEATURE_STATE) as? State

        fun State.toParcelable() =
            Bundle().apply { putParcelable(KEY_FEATURE_STATE, this@toParcelable) }
    }
}