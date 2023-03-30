package co.softov.morestuff.android.di

import co.softov.morestuff.android.app.navigation.AppRouter
import com.github.terrakok.cicerone.Cicerone
import org.koin.dsl.module

private val cicerone = Cicerone.create(AppRouter())

val navigationModule = module {

    single { cicerone.router }

    single { cicerone.getNavigatorHolder() }

}

