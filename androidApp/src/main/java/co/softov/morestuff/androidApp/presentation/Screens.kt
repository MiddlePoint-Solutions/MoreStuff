package co.softov.morestuff.androidApp.presentation

import co.softov.morestuff.androidApp.presentation.content.ContentFragment
import co.softov.morestuff.androidApp.presentation.settings.MainSettings
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    val Content = FragmentScreen("Content") { ContentFragment() }

    val Settings = FragmentScreen("Settings") { MainSettings() }

}