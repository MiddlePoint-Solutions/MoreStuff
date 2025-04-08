package io.middlepoint.morestuff.shared.data.middleware

import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction.CompleteTasksAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction.CreateHintTask
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction.CreateUserTaskAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction.DeleteTasksAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction.RemoveTasksFromScopeAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction.TaskCreatedAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction.UpdateTaskTitleAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction.UpdateTasksToScopeAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateHintTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.DeleteTasksUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.RemoveTasksFromScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.SetTaskCompleteUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.TaskParams
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTaskTitleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTasksScopeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
          onTaskCreated?.invoke(task)
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
