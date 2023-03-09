package co.softov.morestuff.android.ui

import co.softov.morestuff.android.ui.chat.TaskChatFragment
import co.softov.morestuff.android.ui.settings.MainSettings
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    //val Content = FragmentScreen("Content") { MainFragment() }

    val Settings = FragmentScreen("Settings") { MainSettings() }

    val TaskChat = FragmentScreen("TaskChat") { TaskChatFragment() }

}