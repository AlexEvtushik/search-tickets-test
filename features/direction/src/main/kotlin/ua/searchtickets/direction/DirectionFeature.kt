package ua.searchtickets.direction

import android.os.Bundle
import android.os.Parcelable
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.mvicore.feature.ActorReducerFeature
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.parcelize.Parcelize
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.errors.DirectionFromEmptyError
import ua.searchtickets.common.errors.DirectionTheSameError
import ua.searchtickets.common.errors.DirectionToEmptyError
import ua.searchtickets.common.rxjava.onError
import ua.searchtickets.direction.DirectionFeature.Effect
import ua.searchtickets.direction.DirectionFeature.News
import ua.searchtickets.direction.DirectionFeature.State
import ua.searchtickets.direction.DirectionFeature.Wish
import ua.searchtickets.domain.entities.CityEntity
import com.badoo.mvicore.element.Actor as BaseActor
import com.badoo.mvicore.element.NewsPublisher as BaseNewsPublisher
import com.badoo.mvicore.element.Reducer as BaseReducer

class DirectionFeature(timeCapsule: AndroidTimeCapsule) :
    ActorReducerFeature<Wish, Effect, State, News>(
        initialState = timeCapsule.state() ?: State(),
        actor = Actor,
        reducer = Reducer,
        newsPublisher = NewsPublisher
    ) {

    init {
        timeCapsule.register(KEY_TIME_CAPSULE) { state.toParcelable() }
    }

    @Parcelize
    data class State(
        val directionFrom: CityEntity? = null,
        val directionTo: CityEntity? = null,
        val error: Throwable? = null
    ) : Parcelable

    sealed class Wish {
        object ChooseDirectionFrom : Wish()
        object ChooseDirectionTo : Wish()
        object SearchTickets : Wish()
        class ChangeDirection(val city: CityEntity, val directionType: DirectionType) : Wish()
    }

    sealed class Effect {
        object NoEffect : Effect()
        class DirectionFromChanged(val city: CityEntity) : Effect()
        class DirectionToChanged(val city: CityEntity) : Effect()
        class SearchTicketsShown(
            val directionFrom: CityEntity,
            val directionTo: CityEntity
        ) : Effect()

        class ErrorOccurred(val error: Throwable) : Effect()
        object ClearError : Effect()
    }

    sealed class News {
        object DirectionFromClicked : News()
        object DirectionToClicked : News()
        class SearchTicketsClicked(
            val directionFrom: CityEntity,
            val directionTo: CityEntity
        ) : News()
    }

    object Actor : BaseActor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<out Effect> = when (wish) {
            Wish.ChooseDirectionFrom -> noEffect()
            Wish.ChooseDirectionTo -> noEffect()
            Wish.SearchTickets -> searchTickets(state)
            is Wish.ChangeDirection -> changeDirection(wish.city, wish.directionType)
        }
            .onError(Effect.ClearError) { error -> Effect.ErrorOccurred(error) }
            .observeOn(AndroidSchedulers.mainThread())

        private fun searchTickets(state: State): Observable<Effect> = when {
            state.directionFrom == null -> Observable.error(DirectionFromEmptyError())
            state.directionTo == null -> Observable.error(DirectionToEmptyError())
            state.directionFrom.id == state.directionTo.id -> Observable.error(DirectionTheSameError())
            else -> Observable.just(
                Effect.SearchTicketsShown(
                    state.directionFrom,
                    state.directionTo
                )
            )
        }

        private fun changeDirection(
            city: CityEntity,
            directionType: DirectionType
        ): Observable<Effect> = when (directionType) {
            DirectionType.From -> Observable.just(Effect.DirectionFromChanged(city))
            DirectionType.To -> Observable.just(Effect.DirectionToChanged(city))
        }

        private fun noEffect(): Observable<Effect> = Observable.just(Effect.NoEffect)
    }

    object Reducer : BaseReducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State = when (effect) {
            Effect.NoEffect -> state
            is Effect.SearchTicketsShown -> state
            is Effect.DirectionFromChanged -> state.copy(directionFrom = effect.city)
            is Effect.DirectionToChanged -> state.copy(directionTo = effect.city)
            Effect.ClearError -> state.copy(error = null)
            is Effect.ErrorOccurred -> state.copy(error = effect.error)
        }
    }

    object NewsPublisher : BaseNewsPublisher<Wish, Effect, State, News> {
        override fun invoke(wish: Wish, effect: Effect, state: State): News? = when {
            wish == Wish.ChooseDirectionFrom -> News.DirectionFromClicked
            wish == Wish.ChooseDirectionTo -> News.DirectionToClicked
            effect is Effect.SearchTicketsShown -> News.SearchTicketsClicked(
                effect.directionFrom,
                effect.directionTo
            )
            else -> null
        }
    }

    private companion object {
        const val KEY_TIME_CAPSULE = "key:direction_time_capsule"
        const val KEY_FEATURE_STATE = "key:direction_feature_state"

        fun AndroidTimeCapsule.state() =
            get<Bundle>(KEY_TIME_CAPSULE)?.getParcelable(KEY_FEATURE_STATE) as? State

        fun State.toParcelable() =
            Bundle().apply { putParcelable(KEY_FEATURE_STATE, this@toParcelable) }
    }
}