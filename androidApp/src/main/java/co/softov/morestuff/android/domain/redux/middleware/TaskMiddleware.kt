package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.AddTasksToScopeAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.CompleteTasksAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.CreateHintTask
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.CreateUserTaskAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.DeleteTasksAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.RemoveTasksFromScopeAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.UpdateTaskTitleAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.task.AddTasksToScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.CreateHintTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.DeleteTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.RemoveTasksFromScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.SetTaskCompleteUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
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

    data class AddTasksToScopeAction(val taskIds: List<Long>, val scopeId: Long) : TaskAction()
    data class RemoveTasksFromScopeAction(val taskIds: List<Long>, val scopeId: Long) : TaskAction()

}

class TaskMiddleware(
    private val createTaskUseCase: CreateTaskUseCase,
    private val setTaskCompleteUseCase: SetTaskCompleteUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val createHintTaskUseCase: CreateHintTaskUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
    private val addTasksToScopeUseCase: AddTasksToScopeUseCase,
    private val removeTasksFromScopeUseCase: RemoveTasksFromScopeUseCase,
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

            is AddTasksToScopeAction -> scope.launch {
                addTasksToScopeUseCase(action.taskIds, action.scopeId)
            }

            is RemoveTasksFromScopeAction -> scope.launch {
                removeTasksFromScopeUseCase(action.taskIds, action.scopeId)
            }

            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}

