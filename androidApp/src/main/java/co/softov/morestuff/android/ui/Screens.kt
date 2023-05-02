package co.softov.morestuff.android.ui

import co.softov.morestuff.android.app.navigation.BottomSheetFragmentScreen
import co.softov.morestuff.android.ui.ScreenKey.LAUNCHED_TASK_CHAT
import co.softov.morestuff.android.ui.ScreenKey.TASK_CHAT
import co.softov.morestuff.android.ui.chat.task.TaskChatFragment
import co.softov.morestuff.android.ui.list.ListsFragment
import co.softov.morestuff.android.ui.review.ReviewFragment
import co.softov.morestuff.android.ui.schedule.PriorityFragment
import co.softov.morestuff.android.ui.settings.SettingsFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun taskChat(taskId: Long) =
        FragmentScreen("$TASK_CHAT$taskId") { TaskChatFragment.newInstance(taskId) }

    fun launchedTaskChat(taskId: Long) =
        FragmentScreen("$LAUNCHED_TASK_CHAT$taskId") { TaskChatFragment.newInstance(taskId) }

    val ComposeSettings = FragmentScreen("ComposeSettings") { SettingsFragment() }

    val review = FragmentScreen("Review") { ReviewFragment() }

    val priority = FragmentScreen("Priority") { PriorityFragment() }

    val taskLists = BottomSheetFragmentScreen("TaskLists") { ListsFragment() }

}

object ScreenKey {

    const val TASK_CHAT = "TASK_CHAT-"
    const val LAUNCHED_TASK_CHAT = "LAUNCHED_TASK_CHAT-"

}