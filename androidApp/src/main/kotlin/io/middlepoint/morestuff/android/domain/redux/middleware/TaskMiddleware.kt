package io.middlepoint.morestuff.android.domain.redux.middleware

import io.middlepoint.morestuff.android.domain.enums.TaskType
import io.middlepoint.morestuff.android.domain.model.Priority
import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.redux.AppState
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction.UpdateTasksToScopeAction
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction.CompleteTasksAction
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction.CreateHintTask
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction.CreateUserTaskAction
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction.DeleteTasksAction
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction.RemoveTasksFromScopeAction
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction.TaskCreatedAction
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction.UpdateTaskTitleAction
import io.middlepoint.morestuff.android.domain.redux.store.Action
import io.middlepoint.morestuff.android.domain.redux.store.Dispatch
import io.middlepoint.morestuff.android.domain.redux.store.Next
import io.middlepoint.morestuff.android.domain.redux.store.NoOp
import io.middlepoint.morestuff.android.domain.usecase.task.AddTasksToScopeUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.CreateHintTaskUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.CreateTaskUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.DeleteTasksUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.RemoveTasksFromScopeUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.UpdateTasksScopeUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.SetTaskCompleteUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.TaskParams
import io.middlepoint.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class TaskAction : Action.FeatureAction() {

    data class CreateUserTaskAction(
        val title: String,
        val priority: Priority,
        val scopeId: Long,
    ) : TaskAction()

    data class CompleteTasksAction(val taskIds: List<Long>, val complete: Boolean) : TaskAction()

    data class UpdateTaskTitleAction(val taskId: Long, val title: String) : TaskAction()

    data object CreateHintTask : TaskAction()

    data class DeleteTasksAction(val taskIds: List<Long>) : TaskAction()

    internal data class TaskCreatedAction(
        val task: TaskDomain,
        val priority: Priority,
    ) : TaskAction()

    data class UpdateTasksToScopeAction(val taskIds: List<Long>, val scopeId: Long) : TaskAction()
    data class RemoveTasksFromScopeAction(val taskIds: List<Long>, val scopeId: Long) : TaskAction()

}

class TaskMiddleware(
    private val createTaskUseCase: CreateTaskUseCase,
    private val setTaskCompleteUseCase: SetTaskCompleteUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val createHintTaskUseCase: CreateHintTaskUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
    private val removeTasksFromScopeUseCase: RemoveTasksFromScopeUseCase,
    private val updateTasksScopeUseCase: UpdateTasksScopeUseCase,
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
                    val params = TaskParams(title, priority, TaskType.User, scopeId)
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

            is UpdateTasksToScopeAction -> scope.launch {
                updateTasksScopeUseCase(action.taskIds, action.scopeId)
            }

            is RemoveTasksFromScopeAction -> scope.launch {
                removeTasksFromScopeUseCase(action.taskIds, action.scopeId)
            }

            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}
