package co.softov.morestuff.android.ui.chat.task

import android.os.Bundle
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import co.softov.morestuff.android.app.presentation.compose.BaseComposeFragment

class TaskChatFragment : BaseComposeFragment() {

    @Composable
    override fun ScreenContent(
        args: Bundle?,
        showKeyboard: () -> Unit,
        hideKeyboard: () -> Unit,
        dismissDialog: () -> Unit
    ) {
        val taskId = args?.getLong(EXTRA_TASK_ID) ?: 0L
        val nestedScrollInterop = rememberNestedScrollInteropConnection()
        TaskChatContent(
            taskId,
            modifier = Modifier
                .systemBarsPadding()
                .nestedScroll(nestedScrollInterop)
        )
    }

    companion object {

        private const val EXTRA_TASK_ID = "EXTRA_TASK_ID"

        fun newInstance(taskId: Long): TaskChatFragment {
            val args = Bundle()
            args.putLong(EXTRA_TASK_ID, taskId)
            val fragment = TaskChatFragment()
            fragment.arguments = args
            return fragment
        }

    }

}