package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.app.extensions.simpleName
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

    data class CreateTask(val title: String) : TaskAction() {
        override val log: String
            get() = "${this.simpleName}(title=$title)"
    }

    data class CompleteTaskAction(val taskId: Long, val complete: Boolean) : TaskAction()
    data class CompleteTasksAction(val taskIds: List<Long>, val complete: Boolean) : TaskAction()

    internal data class TaskCreatedAction(
        val task: TaskDomain,
        val priority: Priority
    ) : TaskAction() {
        override val log: String
            get() = "${this.simpleName}($task,$priority)"
    }
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
            is CreateTask -> scope.launch {
                val params = TaskParams(action.title)
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