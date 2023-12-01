package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.ScopeAll
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.store.Action

data class TaskState(
    val scopes: List<ScopeDomain> = listOf(),
    val currentScope: ScopeDomain = ScopeAll,
    val selected: List<Long> = listOf(),
)

sealed class TaskAction : Action.FeatureAction() {

    data class CreateUserTaskAction(
        val title: String,
        val priority: Priority,
    ) : TaskAction()

    data class CompleteTasksAction(val taskIds: List<Long>, val complete: Boolean) : TaskAction()

    data class UpdateTaskTitleAction(val taskId: Long, val title: String) : TaskAction()

    data object CreateHintTask : TaskAction()

    data class DeleteTasksAction(val taskIds: List<Long>) : TaskAction()

    internal data class TaskCreatedAction(
        val task: TaskDomain,
        val priority: Priority,
    ) : TaskAction()

    data class InsertTaskIntoScopeAction(val taskId: Long, val scopeId: Long) : TaskAction()
    data class RemoveTaskFromScopeAction(val taskId: Long, val scopeId: Long) : TaskAction()

    data class ToggleTaskSelection(val taskId: Long): TaskAction()

}

fun AppState.reduceTaskState(action: Action): AppState {
    return when (action) {
        is TaskAction -> copy(tasks = tasks.reduce(action))
        else -> this
    }
}

fun TaskState.reduce(action: TaskAction): TaskState {

    return this
}