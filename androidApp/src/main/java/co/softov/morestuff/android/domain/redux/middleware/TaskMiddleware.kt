package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.*
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.SetTasksCompleteUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class TaskAction : Action.FeatureAction() {

    data class CreateUserTaskAction(
        val title: String,
        val priority: Priority
    ) : TaskAction()

    data class CreateSystemTaskAction(
        val title: String,
        val priority: Priority
    ) : TaskAction()

    data class CompleteTaskAction(val taskId: Long, val complete: Boolean) : TaskAction()
    data class CompleteTasksAction(val taskIds: List<Long>, val complete: Boolean) : TaskAction()

    internal data class TaskCreatedAction(
        val task: TaskDomain,
        val priority: Priority
    ) : TaskAction()
}

class TaskMiddleware(
    private val createTaskUseCase: CreateTaskUseCase,
    private val setTaskCompleteUseCase: SetTasksCompleteUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is CreateUserTaskAction -> scope.launch {
                // TODO: Priority score calculation result with task priority
                val params = TaskParams(action.title, 0, TaskType.User)
                val priority = state.priorityState.current
                createTaskUseCase(params).map { task ->
                    dispatch(TaskCreatedAction(task, priority))
                }
            }

            is CreateSystemTaskAction -> scope.launch {
                val params = TaskParams(action.title, 0, TaskType.System)
                val priority = state.priorityState.current
                createTaskUseCase(params).map { task ->
                    dispatch(TaskCreatedAction(task, priority))
                }
            }

            is CompleteTaskAction -> scope.launch {
                with(action) {
                    setTaskCompleteUseCase(listOf(taskId), complete)
                }
            }

            is CompleteTasksAction -> scope.launch {
                with(action) {
                    setTaskCompleteUseCase(taskIds, complete)
                }
            }

            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}