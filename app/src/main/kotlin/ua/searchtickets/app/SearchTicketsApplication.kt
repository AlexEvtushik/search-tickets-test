package ua.searchtickets.app

import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ua.searchtickets.app.di.modules

class SearchTicketsApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        setupDi()
    }

    private fun setupDi() {
        startKoin {
            androidLogger()
            androidContext(this@SearchTicketsApplication)
            modules(modules)
        }
    }
}