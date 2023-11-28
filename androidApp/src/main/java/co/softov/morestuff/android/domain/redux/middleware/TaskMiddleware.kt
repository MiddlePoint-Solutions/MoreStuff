package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.*
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.task.CreateHintTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.DeleteTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTasksForGivenScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.InsertTaskIntoScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.RemoveTaskFromScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.SetTaskCompleteUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

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

    data class GetTasksForGivenScopeAction(val scopeId: Long) : TaskAction()
    data class InsertTaskIntoScopeAction(val taskId: Long, val scopeId: Long) : TaskAction()
    data class RemoveTaskFromScopeAction(val taskId: Long, val scopeId: Long) : TaskAction()

}


class TaskMiddleware(
    private val createTaskUseCase: CreateTaskUseCase,
    private val setTaskCompleteUseCase: SetTaskCompleteUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val createHintTaskUseCase: CreateHintTaskUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
    private val getTasksForGivenScopeUseCase: GetTasksForGivenScopeUseCase,
    private val insertTaskIntoScopeUseCase: InsertTaskIntoScopeUseCase,
    private val removeTaskFromScopeUseCase: RemoveTaskFromScopeUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {
            is CreateUserTaskAction -> scope.launch {
                with(action) {
                    val params = TaskParams(title, priority, TaskType.User)
                    val task = createTaskUseCase(params)
                    dispatch(TaskCreatedAction(task, priority))
                }
            }

            is CompleteTasksAction -> with(action) {
                scope.launch {
                    setTaskCompleteUseCase(taskIds, complete)
                }
            }

            is UpdateTaskTitleAction -> scope.launch {
                updateTaskTitleUseCase(action.taskId, action.title)
            }

            is CreateHintTask -> scope.launch {
                createHintTaskUseCase()
            }

            is DeleteTasksAction -> scope.launch {
                deleteTasksUseCase(action.taskIds)
            }

            is GetTasksForGivenScopeAction -> scope.launch {
                Timber.d(" Scope Manejando acción GetTasksForGivenScopeAction para scopeId: ${action.scopeId}")
                getTasksForGivenScopeUseCase(action.scopeId)
            }

            is InsertTaskIntoScopeAction -> scope.launch {
                insertTaskIntoScopeUseCase(action.taskId, action.scopeId)
            }

            is RemoveTaskFromScopeAction -> scope.launch {
                removeTaskFromScopeUseCase(action.taskId, action.scopeId)
            }

            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}