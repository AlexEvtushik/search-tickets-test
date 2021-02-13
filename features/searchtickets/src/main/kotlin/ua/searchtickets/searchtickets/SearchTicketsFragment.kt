package ua.searchtickets.searchtickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badoo.mvicore.android.AndroidBindings
import org.koin.androidx.scope.ScopeFragment
import org.koin.core.parameter.parametersOf
import ua.searchtickets.common.platform.argumentNotNull
import ua.searchtickets.domain.entities.CityEntity

class SearchTicketsFragment : ScopeFragment() {

    companion object {
        fun newInstance(
            directionFrom: CityEntity,
            directionTo: CityEntity
        ) = SearchTicketsFragment().apply {
            this.directionFrom = directionFrom
            this.directionTo = directionTo
        }
    }

    private val mviView: SearchTicketsView by inject()
    private val bindings: AndroidBindings<SearchTicketsView> by inject {
        parametersOf(this, directionFrom, directionTo)
    }
    private var directionFrom: CityEntity by argumentNotNull()
    private var directionTo: CityEntity by argumentNotNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings.setup(mviView)
        lifecycle.addObserver(mviView)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = mviView.createView(inflater, container)

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mviView)
    }
}