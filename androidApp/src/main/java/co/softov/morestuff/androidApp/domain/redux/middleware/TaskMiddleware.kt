package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.app.extensions.simpleName
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.Task
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.ScheduleReplyAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.*
import co.softov.morestuff.androidApp.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.androidApp.domain.usecase.task.SetTaskCompleteUseCase
import co.softov.morestuff.androidApp.domain.usecase.task.TaskParams
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class TaskAction : Action.FeatureAction() {

    data class CreateTaskAction(val title: String, val priority: Priority) : TaskAction() {
        override val log: String
            get() = "${this.simpleName}(title=$title, priority=${priority.simpleName})"
    }

    data class TaskCompleteAction(val taskId: Long) : TaskAction()

    internal data class TaskCreatedAction(val task: Task, val priority: Priority) : TaskAction() {
        override val log: String
            get() = "${this.simpleName}(task=$task)"
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
            is CreateTaskAction -> scope.launch {
                val params = TaskParams(action.title)
                createTaskUseCase(params).map { task ->
                    dispatch(TaskCreatedAction(task, action.priority))
                }
            }

            is TaskCompleteAction -> scope.launch {
                setTaskCompleteUseCase(action.taskId)
            }

            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}