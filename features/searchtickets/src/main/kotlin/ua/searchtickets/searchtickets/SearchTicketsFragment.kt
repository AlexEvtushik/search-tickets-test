package ua.searchtickets.searchtickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badoo.mvicore.android.AndroidTimeCapsule
import org.koin.androidx.scope.ScopeFragment
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.core.scope.newScope
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

    private lateinit var timeCapsule: AndroidTimeCapsule

    private val mviView: SearchTicketsView by inject()
    private val bindings: SearchTicketsBindings by inject {
        parametersOf(this, timeCapsule, directionFrom, directionTo)
    }
    private var directionFrom: CityEntity by argumentNotNull()
    private var directionTo: CityEntity by argumentNotNull()

    override val scope: Scope by lazy { newScope(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeCapsule = AndroidTimeCapsule(savedInstanceState)
        bindings.setup(mviView)
        lifecycle.addObserver(mviView)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = mviView.createView(inflater, container)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timeCapsule.saveState(outState)
    }

    override fun onDestroy() {
        lifecycle.removeObserver(mviView)
        scope.close()
        super.onDestroy()
    }
}