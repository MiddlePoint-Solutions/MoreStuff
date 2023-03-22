package co.softov.morestuff.android.ui

import co.softov.morestuff.android.ui.ScreenKey.LAUNCHED_TASK_CHAT
import co.softov.morestuff.android.ui.ScreenKey.TASK_CHAT
import co.softov.morestuff.android.ui.chat.task.TaskChatFragment
import co.softov.morestuff.android.ui.review.ReviewFragment
import co.softov.morestuff.android.ui.settings.MainSettings
import co.softov.morestuff.android.ui.settings.SettingsFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    //val Content = FragmentScreen("Content") { MainFragment() }
    fun taskChat(taskId: Long) =
        FragmentScreen("$TASK_CHAT$taskId") { TaskChatFragment.newInstance(taskId) }

    fun launchedTaskChat(taskId: Long) =
        FragmentScreen("$LAUNCHED_TASK_CHAT$taskId") { TaskChatFragment.newInstance(taskId) }

    val ComposeSettings = FragmentScreen("ComposeSettings") { SettingsFragment() }

    val reviewPriority = FragmentScreen("ReviewPriority") { ReviewFragment() }


}

object ScreenKey {

    const val TASK_CHAT = "TASK_CHAT-"
    const val LAUNCHED_TASK_CHAT = "LAUNCHED_TASK_CHAT-"

}