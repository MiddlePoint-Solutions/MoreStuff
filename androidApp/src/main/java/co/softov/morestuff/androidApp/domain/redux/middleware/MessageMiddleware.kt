package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.ScheduleAction.ExecuteScheduleAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.androidApp.domain.service.Notifier
import co.softov.morestuff.androidApp.domain.usecase.message.CreateScheduleMessageUseCase
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskMessageUseCase
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MessageMiddleware(
    private val notifier: Notifier,
    private val createTaskMessageUseCase: CreateTaskMessageUseCase,
    private val createTaskConfirmationMessageUseCase: CreateTaskConfirmationMessageUseCase,
    private val createScheduleMessageUseCase: CreateScheduleMessageUseCase
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
            is ExecuteScheduleAction -> scope.launch {
                createScheduleMessageUseCase(action.scheduleId).map { message ->
                    notifier.showScheduleNotification(action.scheduleId, message)
                }
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}