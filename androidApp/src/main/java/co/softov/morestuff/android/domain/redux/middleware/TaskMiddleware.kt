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
import co.softov.morestuff.android.domain.usecase.task.SetTaskCompleteUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class TaskAction : Action.FeatureAction() {

    data class CreateUserTaskAction(
        val title: String,
        val priority: Priority,
    ) : TaskAction()

    data class CompleteTaskAction(val taskId: Long, val complete: Boolean) : TaskAction()

    data class UpdateTaskTitleAction(val taskId: Long, val title: String) : TaskAction()

    data class CreateHintTask(
        val title: String,
        val priority: Priority,
    ) : TaskAction()

    internal data class TaskCreatedAction(
        val task: TaskDomain,
        val priority: Priority,
    ) : TaskAction()

}


class TaskMiddleware(
    private val createTaskUseCase: CreateTaskUseCase,
    private val setTaskCompleteUseCase: SetTaskCompleteUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val createHintTaskUseCase: CreateHintTaskUseCase
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

            is CompleteTaskAction -> scope.launch {
                with(action) {
                    setTaskCompleteUseCase(taskId, complete)
                }
            }

            is UpdateTaskTitleAction -> scope.launch {
                updateTaskTitleUseCase(action.taskId, action.title)
            }

            is CreateHintTask -> scope.launch {
                createHintTaskUseCase(params = TaskParams("Hint Task <---Click me!", Priority.Now(),TaskType.User))
            }

            else -> NoOp
        }
        return next(state, action, dispatch)
    }
}