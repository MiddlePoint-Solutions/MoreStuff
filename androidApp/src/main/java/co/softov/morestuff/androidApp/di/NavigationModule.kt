package co.softov.morestuff.androidApp.di

import co.softov.morestuff.androidApp.app.presentation.fragment.FlowFragmentFactory
import com.github.terrakok.cicerone.Cicerone
import org.koin.dsl.module

private val cicerone = Cicerone.create()

val navigationModule = module {

    single { cicerone.router }

    single { cicerone.getNavigatorHolder() }

    // Fragment Factory
    single { FlowFragmentFactory(navigatorHolder = get()) }
}