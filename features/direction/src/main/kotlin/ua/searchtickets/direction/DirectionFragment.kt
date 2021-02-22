package ua.searchtickets.direction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.scope.ScopeFragment
import org.koin.core.parameter.parametersOf

class DirectionFragment : ScopeFragment() {

    companion object {
        fun newInstance() = DirectionFragment()
    }

    private val mviView: DirectionView by inject()
    private val bindings: DirectionBindings by inject { parametersOf(this) }

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
        lifecycle.removeObserver(mviView)
        super.onDestroy()
    }
}