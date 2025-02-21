package io.middlepoint.morestuff.shared.ui.screen.schedule

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

sealed class ScopeTasksModels {
    data object Loading : ScopeTasksModels()
    data class Data(val tasks: List<TaskUiModel>) : ScopeTasksModels()
}



@Immutable
sealed class ScopeTasksEvent {
    data class ReorderTasks(val updatedTasks: List<TaskUiModel>) : ScopeTasksEvent()

}