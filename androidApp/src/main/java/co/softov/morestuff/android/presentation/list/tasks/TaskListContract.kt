package co.softov.morestuff.android.presentation.list.tasks

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.presentation.list.tasks.model.TaskListItemViewModel

data class TaskListViewState(
    val data: List<TaskListItemViewModel> = listOf()
) : BaseViewState

sealed class TaskListViewEvent :
    BaseViewEvent {
    data class UpdateTasks(val data: List<TaskListItemViewModel>) : TaskListViewEvent()
}