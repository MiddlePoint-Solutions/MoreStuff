package io.middlepoint.morestuff.shared.ui.screen.chat.task


import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class TaskChatPresenter(
    private val taskId: Long,
    private val scopeId: Long? = null
) : MoleculeViewModel<TaskChatEvent, TaskChatState>() {

    override val initialState: TaskChatState = TaskChatState()

    @Composable
    override fun models(events: SharedFlow<TaskChatEvent>): TaskChatState {
        return taskChatModel(taskId, scopeId, initialState, events)
    }

}
