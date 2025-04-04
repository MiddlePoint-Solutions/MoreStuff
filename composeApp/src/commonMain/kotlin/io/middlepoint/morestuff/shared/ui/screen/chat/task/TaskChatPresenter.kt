package io.middlepoint.morestuff.shared.ui.screen.chat.task


import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class TaskChatPresenter(
    private val taskId: Long,
    ) : MoleculeViewModel<TaskChatEvent, TaskChatState>() {

    override val initialState: TaskChatState = TaskChatState()
    @Composable
    override fun models(events: SharedFlow<TaskChatEvent>): TaskChatState {
        return taskChatModel(taskId, initialState, events)
    }

}
