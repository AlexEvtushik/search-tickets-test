package ua.searchtickets.direction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo
import ua.searchtickets.common.errors.DirectionFromEmptyError
import ua.searchtickets.common.errors.DirectionTheSameError
import ua.searchtickets.common.errors.DirectionToEmptyError
import ua.searchtickets.common.platform.didSet
import ua.searchtickets.common.vendor.mvicore.MviCoreView
import ua.searchtickets.domain.entities.CityEntity

class DirectionView : MviCoreView<DirectionView.UiEvent, DirectionView.ViewModel>() {

    sealed class UiEvent {
        object DirectionFromClicked : UiEvent()
        object DirectionToClicked : UiEvent()
        object SearchTicketsClicked : UiEvent()
    }

    data class ViewModel(
        val directionFrom: CityEntity?,
        val directionTo: CityEntity?,
        val error: Throwable?
    )

    private var textDirectionFrom by didSet<TextView> {
        states.map { it.directionFrom?.fullname ?: resources.getString(R.string.from_label) }
            .distinctUntilChanged()
            .subscribe(::setText)
            .addTo(disposeBag)

        states.map {
            ContextCompat.getColor(
                context,
                if (it.directionFrom?.fullname?.isNotEmpty() == true) android.R.color.black
                else android.R.color.darker_gray
            )
        }
            .distinctUntilChanged()
            .subscribe(::setTextColor)
            .addTo(disposeBag)

        clicks().map { UiEvent.DirectionFromClicked }
            .subscribe(uiEvents)
            .addTo(disposeBag)
    }
    private var textDirectionTo by didSet<TextView> {
        states.map { it.directionTo?.fullname ?: resources.getString(R.string.to_label) }
            .distinctUntilChanged()
            .subscribe(::setText)
            .addTo(disposeBag)

        states.map {
            ContextCompat.getColor(
                context,
                if (it.directionTo?.fullname?.isNotEmpty() == true) android.R.color.black
                else android.R.color.darker_gray
            )
        }
            .distinctUntilChanged()
            .subscribe(::setTextColor)
            .addTo(disposeBag)

        clicks().map { UiEvent.DirectionToClicked }
            .subscribe(uiEvents)
            .addTo(disposeBag)
    }
    private var buttonSearchTickets by didSet<Button> {
        clicks().map { UiEvent.SearchTicketsClicked }
            .subscribe(uiEvents)
            .addTo(disposeBag)
    }

    private var errorNotifications: Snackbar? = null

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View =
        inflater.inflate(R.layout.fragment_direction, container, false)

    override fun bindViews(rootView: View) {
        rootView.apply {
            textDirectionFrom = rootView.findViewById(R.id.textview_direction_from_city)
            textDirectionTo = rootView.findViewById(R.id.textview_direction_to_city)
            buttonSearchTickets = rootView.findViewById(R.id.button_direction_search_tickets)

            configureNotifications(this)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        errorNotifications?.dismiss()
        errorNotifications = null
    }

    private fun configureNotifications(view: View) {
        states.distinctUntilChanged { currentState, newState ->
            currentState.error == newState.error
        }
            .subscribe { state ->
                errorNotifications =
                    if (state.error != null && view.isShown && errorNotifications?.isShown != true) {
                        Snackbar.make(
                            view,
                            when (state.error) {
                                is DirectionFromEmptyError -> R.string.direction_from_is_empty_error
                                is DirectionToEmptyError -> R.string.direction_to_is_empty_error
                                is DirectionTheSameError -> R.string.direction_the_same_error
                                else -> R.string.something_went_wrong_error
                            },
                            Snackbar.LENGTH_INDEFINITE
                        ).apply { show() }
                    } else {
                        errorNotifications?.dismiss()
                        null
                    }
            }
            .addTo(disposeBag)
    }
}