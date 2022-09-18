package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.redux.middleware.ResponseAction.ScheduleReplyAction
import co.softov.morestuff.android.domain.redux.middleware.ResponseAction.UserResponseAction
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
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
                    ifRight = { schedule ->
                        dispatch(ScheduleReplyAction(schedule, action.replyType))
                    },
                    ifLeft = {
                        Timber.e(it.toString())
                    }
                )
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}