package io.middlepoint.morestuff.shared.data.middleware

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
        scope.launch {
            when (action) {
                is TaskAction.CreateUserTaskAction -> {
                    logActivityUseCase("User created task \"${action.title}\"", "{}")
                }
                is TaskAction.UpdateTaskTitleAction -> {
                    logActivityUseCase("User updated task title to \"${action.title}\"", "{}")
                }
                is TaskAction.SetTaskCompletedAction -> {
                    if (action.completed) {
                        logActivityUseCase("User completed a task", "{}")
                    }
                }
                is TaskAction.DeleteAction -> {
                    logActivityUseCase("User deleted a task", "{}")
                }
                // Not all actions are logged yet.
                else -> NoOp
            }
        }
        return next(state, action, dispatch)
    }
}
