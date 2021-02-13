package ua.searchtickets.app.di

import androidx.fragment.app.FragmentActivity
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.Router
import ua.searchtickets.app.navigation.AppNavigator
import ua.searchtickets.app.navigation.AppRouter
import ua.searchtickets.app.navigation.RootCoordinator
import ua.searchtickets.common.navigation.Coordinator

val navigationModule = module {
    val cicerone = Cicerone.create(AppRouter())
    single<Router> { cicerone.router } bind AppRouter::class
    single { RootCoordinator(get()) } bind Coordinator::class
    single { cicerone.navigatorHolder }
    factory<Navigator> { (activity: FragmentActivity, containerId: Int) ->
        AppNavigator(activity, containerId)
    }
}
