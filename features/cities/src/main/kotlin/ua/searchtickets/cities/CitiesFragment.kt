package ua.searchtickets.cities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.scope.ScopeFragment
import org.koin.core.parameter.parametersOf
import ua.searchtickets.common.entities.DirectionType
import ua.searchtickets.common.platform.argumentNotNull
import ua.searchtickets.common.sharedfeature.EventId

class CitiesFragment : ScopeFragment() {

    companion object {
        fun newInstance(
            directionType: DirectionType,
            outEventId: EventId
        ) = CitiesFragment().apply {
            this.outEventId = outEventId
            this.directionType = directionType
        }
    }

    private val mviView: CitiesView by inject {
        parametersOf(requireActivity() as AppCompatActivity)
    }
    private val bindings: CitiesBindings by inject {
        parametersOf(this, directionType, outEventId)
    }
    private var directionType: DirectionType by argumentNotNull()
    private var outEventId: EventId by argumentNotNull()

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