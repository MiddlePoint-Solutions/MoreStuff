package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.app.extensions.simpleName
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.redux.Action
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.NoOp
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.*
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.SetTaskCompleteUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Middleware
import co.softov.morestuff.android.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class TaskAction : Action.FeatureAction() {

    data class CreateTask(val title: String) : TaskAction() {
        override val log: String
            get() = "${this.simpleName}(title=$title)"
    }

    data class TaskComplete(val taskId: Long) : TaskAction()

    internal data class TaskCreatedAction(val task: Task, val priority: Priority) : TaskAction() {
        override val log: String
            get() = "${this.simpleName}($task,$priority)"
    }
}

class TaskMiddleware(
    private val createTaskUseCase: CreateTaskUseCase,
    private val setTaskCompleteUseCase: SetTaskCompleteUseCase
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

            is TaskComplete -> scope.launch {
                setTaskCompleteUseCase(action.taskId)
            }

            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}