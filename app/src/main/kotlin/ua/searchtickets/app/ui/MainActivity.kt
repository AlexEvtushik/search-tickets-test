package ua.searchtickets.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ua.searchtickets.R
import ua.searchtickets.app.navigation.RootCoordinator
import ua.searchtickets.common.navigation.Coordinator

class MainActivity : AppCompatActivity() {
    private val coordinator: Coordinator by inject()
    private val navigatorHolder: NavigatorHolder by inject()
    private val navigator: Navigator by inject {
        parametersOf(this, R.id.framelayout_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coordinator.accept(RootCoordinator.MainNavigationEvent.AppStarted)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }
}