package io.middlepoint.morestuff.android.ui.schedule

import io.middlepoint.morestuff.android.ui.model.TaskUiModel

sealed class ScopeTasksModels {
    data object Loading : ScopeTasksModels()
    data class Data(val tasks: List<TaskUiModel>) : ScopeTasksModels()
}