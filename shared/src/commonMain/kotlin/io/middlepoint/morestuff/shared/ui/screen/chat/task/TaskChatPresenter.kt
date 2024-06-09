package io.middlepoint.morestuff.shared.ui.screen.chat.task


import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.Flow

class TaskChatPresenter(
    private val taskId: Long,
    private val savedState: SavedStateHandle
) : MoleculeViewModel<TaskChatEvent, TaskChatState>() {

    override val initialState: TaskChatState
        get() = savedState["TaskChat$taskId"] ?: TaskChatState()

    @Composable
    override fun models(events: Flow<TaskChatEvent>): TaskChatState {
        return taskChatModel(taskId, initialState, events)
    }

    override fun onSaveState(model: TaskChatState) {
//        savedState["TaskChat$taskId"] = model
    }

}
