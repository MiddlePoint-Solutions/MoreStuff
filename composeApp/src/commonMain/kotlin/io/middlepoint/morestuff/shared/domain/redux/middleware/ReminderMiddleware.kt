package io.middlepoint.morestuff.shared.domain.redux.middleware

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.middleware.ReminderAction.UserResponseAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScheduleAction.ScheduleReplyAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ReminderAction : Action.FeatureAction() {

    data class UserResponseAction(
        val scheduleId: Long,
        val replyType: ReplyType,
    ) : ReminderAction()

}

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