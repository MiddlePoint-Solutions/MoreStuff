package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.ScheduleReplyAction
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.UserResponseAction
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetScheduleUseCase
import co.softov.morestuff.androidApp.domain.redux.Dispatch
import co.softov.morestuff.androidApp.domain.redux.Middleware
import co.softov.morestuff.androidApp.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class ResponseAction : Action.FeatureAction() {
    data class UserResponseAction(
        val scheduleId: Long,
        val replyType: ReplyType
    ) : ResponseAction()

    internal data class ScheduleReplyAction(
        val schedule: Schedule,
        val replyType: ReplyType
    ) : ResponseAction()
}

class ResponseMiddleware(
    private val getScheduleUseCase: GetScheduleUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is UserResponseAction -> scope.launch {
                getScheduleUseCase(action.scheduleId).fold(
                    success = { schedule ->
                        dispatch(ScheduleReplyAction(schedule, action.replyType))
                    },
                    failure = {
                        Timber.e(it)
                    }
                )
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}