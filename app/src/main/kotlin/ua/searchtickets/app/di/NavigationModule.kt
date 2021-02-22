package ua.searchtickets.app.di

import androidx.fragment.app.FragmentActivity
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import org.koin.dsl.bind
import org.koin.dsl.module
import ua.searchtickets.app.navigation.RootNavigator
import ua.searchtickets.app.navigation.RootCoordinator
import ua.searchtickets.common.navigation.Coordinator

val navigationModule = module {
    single { Cicerone.create() }
    single { get<Cicerone<Router>>().router }
    single { get<Cicerone<Router>>().getNavigatorHolder() }
    single { RootCoordinator(get()) } bind Coordinator::class
    factory<Navigator> { (activity: FragmentActivity, containerId: Int) ->
        RootNavigator(activity, containerId)
    }
}
