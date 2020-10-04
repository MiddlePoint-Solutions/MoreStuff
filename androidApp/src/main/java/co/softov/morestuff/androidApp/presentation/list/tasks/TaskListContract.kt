package co.softov.morestuff.androidApp.presentation.list.tasks

import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.androidApp.presentation.list.tasks.model.TaskListItemViewModel

data class TaskListViewState(
    val data: List<TaskListItemViewModel> = listOf()
) : BaseViewState

sealed class TaskListViewEvent :
    BaseViewEvent {
    data class UpdateTasks(val data: List<TaskListItemViewModel>) : TaskListViewEvent()
}