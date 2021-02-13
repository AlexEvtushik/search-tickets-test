package ua.searchtickets.common.vendor.mvicore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.ObservableSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

abstract class MviCoreView<UiEvent, State> private constructor(
    protected val uiEvents: Relay<UiEvent>,
    protected val states: Relay<State>
) :
    Consumer<State> by states,
    ObservableSource<UiEvent> by uiEvents,
    DefaultLifecycleObserver {

  constructor() : this(
      uiEvents = PublishRelay.create(),
      states = BehaviorRelay.create()
  )

  protected lateinit var disposeBag: CompositeDisposable

  abstract fun createView(inflater: LayoutInflater, container: ViewGroup?): View

  @CallSuper
  override fun onStart(owner: LifecycleOwner) {
    disposeBag = CompositeDisposable()
    (owner as? Fragment)?.view?.let { bindViews(it) }
  }

  @CallSuper
  override fun onStop(owner: LifecycleOwner) {
    disposeBag.dispose()
  }

  protected abstract fun bindViews(rootView: View)

}
