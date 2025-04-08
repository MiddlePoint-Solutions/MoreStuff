package io.middlepoint.morestuff.shared.data.middleware

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.action.ReminderAction.UserResponseAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction.ScheduleReplyAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ReminderMiddleware(
    private val logger: Logger,
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
                        logger.e(it.toString())
                    }
                )
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }

}