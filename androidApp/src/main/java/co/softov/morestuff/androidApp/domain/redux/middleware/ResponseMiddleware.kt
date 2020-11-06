package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.enums.ReplyType.*
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.ScheduleResponseAction
import co.softov.morestuff.androidApp.domain.usecase.schedule.HandleScheduleResponseUseCase
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ResponseAction : Action.FeatureAction() {
    data class ScheduleResponseAction(val scheduleId: Long, val replyType: ReplyType): ResponseAction()
}

class ResponseMiddleware(
    private val handleScheduleResponseUseCase: HandleScheduleResponseUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is ScheduleResponseAction -> scope.launch {
                // TODO: handle different reply types here, deconstructing the use-case
                handleScheduleResponseUseCase(action.scheduleId, action.replyType)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}