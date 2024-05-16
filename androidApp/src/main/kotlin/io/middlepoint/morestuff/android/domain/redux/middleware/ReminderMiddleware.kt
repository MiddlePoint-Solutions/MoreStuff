package io.middlepoint.morestuff.android.domain.redux.middleware

import io.middlepoint.morestuff.android.domain.enums.ReplyType
import io.middlepoint.morestuff.android.domain.redux.AppState
import io.middlepoint.morestuff.android.domain.redux.store.Dispatch
import io.middlepoint.morestuff.android.domain.redux.store.Next
import io.middlepoint.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import io.middlepoint.morestuff.android.domain.redux.middleware.ScheduleAction.ScheduleReplyAction
import io.middlepoint.morestuff.android.domain.redux.store.Action
import io.middlepoint.morestuff.android.domain.redux.store.NoOp
import io.middlepoint.morestuff.android.domain.usecase.schedule.GetScheduleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class ReminderAction : Action.FeatureAction() {

    data class UserResponseAction(
        val scheduleId: Long,
        val replyType: ReplyType,
    ) : ReminderAction()

}

class ReminderMiddleware(
    private val getScheduleUseCase: GetScheduleUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
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