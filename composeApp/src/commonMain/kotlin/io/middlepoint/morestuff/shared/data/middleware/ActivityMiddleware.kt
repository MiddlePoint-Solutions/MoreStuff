package io.middlepoint.morestuff.shared.data.middleware

import io.middlepoint.morestuff.shared.domain.enums.ActivityType
import io.middlepoint.morestuff.shared.domain.model.ActivityData
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.usecase.activity.LogActivityUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp

class ActivityMiddleware(
  private val logActivityUseCase: LogActivityUseCase
) : Middleware<AppState> {

  override fun invoke(
    state: AppState,
    action: Action,
    dispatch: Dispatch,
    next: Next<AppState>,
    scope: CoroutineScope
  ): Action {
    scope.launch { logActivity(action) }
    return next(state, action, dispatch)
  }

  private suspend fun logActivity(action: Action) {
    when (action) {
      is TaskAction.TaskCreatedAction -> {
        val data = ActivityData(ActivityType.CreateUserTask, listOf(action.task.id))
        logActivityUseCase("User created task \"${action.task.title}\"", data)
      }

      is TaskAction.UpdateTaskTitleAction -> {
        val data = ActivityData(ActivityType.UpdateTaskTitle, listOf(action.taskId))
        logActivityUseCase("User updated task title to \"${action.title}\"", data)
      }

      is TaskAction.CompleteTasksAction -> {
        val type = ActivityData(ActivityType.CompleteTasks, action.taskIds)
        if (action.complete) {
          logActivityUseCase("User completed a tasks", type)
        } else {
          logActivityUseCase("User uncompleted a tasks", type)
        }
      }

      is TaskAction.DeleteTasksAction -> {
        val type = ActivityData(ActivityType.DeleteTasks, action.taskIds)
        logActivityUseCase("User deleted a tasks", type)
      }

      else -> NoOp
    }
  }
}
