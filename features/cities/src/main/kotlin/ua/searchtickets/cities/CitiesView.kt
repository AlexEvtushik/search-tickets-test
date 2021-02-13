package ua.searchtickets.cities

import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import com.jakewharton.rxbinding3.appcompat.navigationClicks
import com.jakewharton.rxbinding3.view.visibility
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom
import ua.searchtickets.common.platform.didSet
import ua.searchtickets.common.platform.recyclerview.HeterogeneousAdapter
import ua.searchtickets.common.vendor.mvicore.MviCoreView
import ua.searchtickets.domain.entities.CityEntity
import java.util.concurrent.TimeUnit

class CitiesView(
    private val activity: AppCompatActivity
) : MviCoreView<CitiesView.UiEvent, CitiesView.ViewModel>() {

    sealed class UiEvent {
        object BackClicked : UiEvent()
        class SearchQueryChanged(val query: String) : UiEvent()
        class CitySelected(val city: CityEntity) : UiEvent()
    }

    data class ViewModel(
        val items: List<HeterogeneousAdapter.Item>,
        val query: String,
        val isLoading: Boolean,
        val error: Throwable?
    )

    private var toolbar by didSet<Toolbar> {
        activity.setSupportActionBar(this)

        navigationClicks().map { UiEvent.BackClicked }
            .subscribe(uiEvents)
            .addTo(disposeBag)
    }
    private var editTextSearch by didSet<EditText> {
        requestFocus()
        showSoftKeyboard()

        states.map { it.query }
            .distinctUntilChanged()
            .filter { it != text.toString() }
            .subscribe(::setText)
            .addTo(disposeBag)

        textChanges().debounce(SEARCH_INPUT_DELAY, TimeUnit.MILLISECONDS)
            .map { it.toString() }
            .withLatestFrom(states)
            .filter { (newText, state) -> newText != state.query }
            .map { (newText, _) -> UiEvent.SearchQueryChanged(newText) }
            .subscribe(uiEvents)
            .addTo(disposeBag)
    }
    private var listCountryCodes by didSet<RecyclerView> {
        adapter = HeterogeneousAdapter().apply {
            addDelegate(
                adapterDelegate<CityItem, HeterogeneousAdapter.Item>(R.layout.item_city) {
                    itemView.setOnClickListener { uiEvents.accept(UiEvent.CitySelected(item.city)) }

                    val textCityName = findViewById<TextView>(R.id.textview_itemcity_name)
                    val textIataCode = findViewById<TextView>(R.id.textview_itemcity_iata_code)

                    bind {
                        textCityName.text = item.city.fullname
                        textIataCode.text = item.city.iata.firstOrNull().orEmpty()
                    }
                }
            )

            states.distinctUntilChanged { currentState, newState ->
                currentState.items.size == newState.items.size &&
                        currentState.items.containsAll(newState.items) &&
                        currentState.items == newState.items
            }
                .subscribe { newState ->
                    accept(newState.items)
                    notifyDataSetChanged()
                }
                .addTo(disposeBag)
        }
    }
    private var textNothingFound by didSet<View> {
        states.map { !it.isLoading && it.items.isEmpty() }
            .distinctUntilChanged()
            .subscribe(visibility())
            .addTo(disposeBag)
    }
    private var progressBar by didSet<ProgressBar> {
        states.map { it.isLoading }
            .distinctUntilChanged()
            .subscribe(visibility())
            .addTo(disposeBag)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View =
        inflater.inflate(R.layout.fragment_cities, container, false)

    override fun bindViews(rootView: View) {
        rootView.apply {
            toolbar = rootView.findViewById(R.id.toolbar_cities)
            listCountryCodes = rootView.findViewById(R.id.recyclerview_cities)
            editTextSearch = rootView.findViewById(R.id.edittext_cities_search)
            textNothingFound = rootView.findViewById(R.id.textview_cities_not_found)
            progressBar = rootView.findViewById(R.id.progressbar_cities)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        (owner as? Fragment)?.view?.hideSoftKeyboard()
    }

    class CityItem(val city: CityEntity) : HeterogeneousAdapter.Item

    private companion object {
        const val SEARCH_INPUT_DELAY = 300L
    }
}