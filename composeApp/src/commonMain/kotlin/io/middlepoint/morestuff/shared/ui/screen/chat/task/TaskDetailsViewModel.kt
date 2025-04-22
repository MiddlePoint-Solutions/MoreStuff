package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow


class TaskDetailsViewModel(
    private val taskId: Uuid,
) : MoleculeViewModel<TaskDetailsEvent, TaskDetailsState>() {

    override val initialState: TaskDetailsState = TaskDetailsState()

    @Composable
    override fun models(events: SharedFlow<TaskDetailsEvent>): TaskDetailsState {
        return taskDetailsModel(
            taskId = taskId,
            initialState = initialState,
            events = events
        )
    }
}

