package co.softov.morestuff.android.ui

import co.softov.morestuff.android.ui.main.ContentFragment
import co.softov.morestuff.android.ui.settings.MainSettings
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    val Content = FragmentScreen("Content") { ContentFragment() }

    val Settings = FragmentScreen("Settings") { MainSettings() }

}