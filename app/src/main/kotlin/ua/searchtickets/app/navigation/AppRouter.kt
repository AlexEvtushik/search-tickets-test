package ua.searchtickets.app.navigation

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.ObservableSource
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.*

class AppRouter private constructor(private val screensSubject: BehaviorRelay<AppScreen>) :
    Router(),
    ObservableSource<AppScreen> by screensSubject {

    constructor() : this(BehaviorRelay.create<AppScreen>())

    private var screens = emptyList<AppScreen>()

    override fun executeCommands(vararg commands: Command) {
        super.executeCommands(*commands)
        commands.forEach { command ->
            when (command) {
                is Back -> screens.takeIf { it.size > 1 }
                    ?.let {
                        screens = screens.dropLast(1)
                        screensSubject.accept(screens.last())
                    }
                is BackTo -> (command.screen as? AppScreen)?.let { screen ->
                    screens.lastIndexOf(screen)
                        .takeIf { it >= 0 }
                        ?.let { index -> screens = screens.slice(0..index) }
                    screensSubject.accept(screen)
                }
                is Forward -> (command.screen as? AppScreen)?.let { screen ->
                    screens = screens + screen
                    screensSubject.accept(screen)
                }
                is Replace -> (command.screen as? AppScreen)?.let { screen ->
                    screens = screens.dropLast(1) + screen
                    screensSubject.accept(screen)
                }
            }
        }
    }
}
