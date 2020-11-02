package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskMessageUseCase
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MessageMiddleware(
    private val createTaskMessageUseCase: CreateTaskMessageUseCase,
    private val createTaskConfirmationMessageUseCase: CreateTaskConfirmationMessageUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {

        when (action) {
            is TaskCreatedAction -> scope.launch {
                createTaskMessageUseCase(action.task)
                createTaskConfirmationMessageUseCase(action.task.id, action.priority)
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}