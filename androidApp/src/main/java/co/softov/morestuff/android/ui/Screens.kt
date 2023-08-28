package co.softov.morestuff.android.ui

//object Screens {
//
//    fun taskChat(taskId: Long) =
//        FragmentScreen("$TASK_CHAT$taskId") { TaskChatFragment.newInstance(taskId) }
//
//    fun launchedTaskChat(taskId: Long) =
//        FragmentScreen("$LAUNCHED_TASK_CHAT$taskId") { TaskChatFragment.newInstance(taskId) }
//
//    val ComposeSettings = FragmentScreen("ComposeSettings") { SettingsFragment() }
//
//    val review = FragmentScreen("Review") { ReviewFragment() }
//
//    val taskLists = BottomSheetFragmentScreen("TaskLists") { ListsFragment() }
//
//}

object ScreenKey {

    const val TASK_CHAT = "TASK_CHAT-"
    const val LAUNCHED_TASK_CHAT = "LAUNCHED_TASK_CHAT-"

}