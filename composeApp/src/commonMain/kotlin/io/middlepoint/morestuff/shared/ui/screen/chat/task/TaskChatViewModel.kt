package io.middlepoint.morestuff.shared.ui.screen.chat.task


import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class TaskChatViewModel(
    private val taskId: Uuid,
    ) : MoleculeViewModel<TaskChatEvent, TaskChatState>() {

    override val initialState: TaskChatState = TaskChatState()

    @Composable
    override fun models(events: SharedFlow<TaskChatEvent>): TaskChatState {
        return taskChatModel(taskId, initialState, events)
    }

}
