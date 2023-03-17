package co.softov.morestuff.android.ui

import co.softov.morestuff.android.ui.chat.TaskChatFragment
import co.softov.morestuff.android.ui.settings.MainSettings
import co.softov.morestuff.android.ui.settings.SettingsFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    //val Content = FragmentScreen("Content") { MainFragment() }

    val Settings = FragmentScreen("Settings") { MainSettings() }

    fun taskChat(taskId: Long) =
        FragmentScreen("TaskChat-$taskId") { TaskChatFragment.newInstance(taskId) }

    val ComposeSettings = FragmentScreen("ComposeSettings"){ SettingsFragment() }
}