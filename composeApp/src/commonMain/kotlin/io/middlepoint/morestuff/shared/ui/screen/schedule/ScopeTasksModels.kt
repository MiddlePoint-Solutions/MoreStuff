package io.middlepoint.morestuff.shared.ui.screen.schedule

import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

sealed class ScopeTasksModels {
    data object Loading : ScopeTasksModels()
    data class Data(val tasks: List<TaskUiModel>) : ScopeTasksModels()
}