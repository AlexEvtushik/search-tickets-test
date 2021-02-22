package ua.searchtickets.direction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.scope.ScopeFragment
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.core.scope.newScope

class DirectionFragment : ScopeFragment() {

    companion object {
        fun newInstance() = DirectionFragment()
    }

    private val mviView: DirectionView by inject()
    private val bindings: DirectionBindings by inject { parametersOf(this) }

    override val scope: Scope by lazy { newScope(this) }

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
        scope.close()
        super.onDestroy()
    }
}