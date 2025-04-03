package io.middlepoint.morestuff.shared.ui.screen.chat.task


import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import io.middlepoint.morestuff.shared.ui.screen.home.AppPresenter
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TaskChatPresenter(
    private val taskId: Long,
    private val scopeId: Long? = null,
    private val appPresenter: AppPresenter,
    ) : MoleculeViewModel<TaskChatEvent, TaskChatState>() {

    override val initialState: TaskChatState = TaskChatState()
    val notifications = appPresenter.notifications.asSharedFlow()

    @Composable
    override fun models(events: SharedFlow<TaskChatEvent>): TaskChatState {
        return taskChatModel(taskId, scopeId, initialState, events, notifications = appPresenter.notifications)
    }

}
