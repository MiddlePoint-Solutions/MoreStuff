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
import co.softov.morestuff.android.domain.usecase.task.DecrementTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetDefaultPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.IncrementTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.SetTasksCompleteUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.domain.usecase.task.UpdatePlannedTasksPriorityUseCase
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
    private val setTaskCompleteUseCase: SetTasksCompleteUseCase,
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
                with(action) {
                    val params = TaskParams(title, priority, TaskType.User)
                    val task = createTaskUseCase(params)
                    dispatch(TaskCreatedAction(task, priority))
                }
            }

            is CreateSystemTaskAction -> scope.launch {
//                val params = TaskParams(action.title, 0, TaskType.System)
                TODO("Create system task")
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