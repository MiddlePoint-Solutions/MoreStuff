package co.softov.morestuff.androidApp.presentation

import co.softov.morestuff.androidApp.presentation.content.ContentFlowFragment
import co.softov.morestuff.androidApp.presentation.content.ContentFragment
import co.softov.morestuff.androidApp.presentation.settings.MainSettings
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    // Flows
    val ContentFlow = FragmentScreen("ContentFlow") { fragmentFactory ->
        fragmentFactory.instantiate(
            ClassLoader.getSystemClassLoader(),
            ContentFlowFragment::javaClass.name
        )
    }

    // Auth
    val Content = FragmentScreen("Content") { ContentFragment() }

    val Settings = FragmentScreen("Settings") { MainSettings() }

}