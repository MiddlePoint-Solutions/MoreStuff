package co.softov.morestuff.android.ui.schedule

import co.softov.morestuff.android.ui.model.TaskUiModel

sealed class ScopeTasksModels {
    data object Loading : ScopeTasksModels()
    data class Data(val tasks: List<TaskUiModel>) : ScopeTasksModels()
}