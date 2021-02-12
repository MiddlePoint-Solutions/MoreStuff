package co.softov.morestuff.android.di

import com.github.terrakok.cicerone.Cicerone
import org.koin.dsl.module

private val cicerone = Cicerone.create()

val navigationModule = module {

    single { cicerone.router }

    single { cicerone.getNavigatorHolder() }

}

