package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.TaskAction.CompleteTasksAction
import co.softov.morestuff.android.domain.redux.state.TaskAction.CreateHintTask
import co.softov.morestuff.android.domain.redux.state.TaskAction.CreateUserTaskAction
import co.softov.morestuff.android.domain.redux.state.TaskAction.DeleteTasksAction
import co.softov.morestuff.android.domain.redux.state.TaskAction.InsertTaskIntoScopeAction
import co.softov.morestuff.android.domain.redux.state.TaskAction.RemoveTaskFromScopeAction
import co.softov.morestuff.android.domain.redux.state.TaskAction.TaskCreatedAction
import co.softov.morestuff.android.domain.redux.state.TaskAction.UpdateTaskTitleAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.task.CreateHintTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.DeleteTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.InsertTaskIntoScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.RemoveTaskFromScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.SetTaskCompleteUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class TaskMiddleware(
    private val createTaskUseCase: CreateTaskUseCase,
    private val setTaskCompleteUseCase: SetTaskCompleteUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val createHintTaskUseCase: CreateHintTaskUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase,
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