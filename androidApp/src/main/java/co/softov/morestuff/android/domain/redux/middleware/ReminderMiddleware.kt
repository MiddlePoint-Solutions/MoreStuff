package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.SmartReminderAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ScheduleReplyAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.message.GetActiveScheduleMessages
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import timber.log.Timber

sealed class ReminderAction : Action.FeatureAction() {

    object SmartReminderAction : ReminderAction()

    data class UserResponseAction(
        val scheduleId: Long,
        val replyType: ReplyType
    ) : ReminderAction()

}

class ReminderMiddleware(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getActiveScheduleMessages: GetActiveScheduleMessages
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is SmartReminderAction -> scope.launch {
                val currentTime = TimeUtils.nowUtcInstant
                val qualifiedForRescheduling = getActiveScheduleMessages().filter {
                    val createdTime = it.createTime.toInstant()
                    createdTime.periodUntil(currentTime, TimeZone.UTC).hours > 1
                }.also {
                    Timber.d("qualifiedForRescheduling: ${it.size}")
                }

                dispatch(
                    ScheduleAction.SmartRescheduleAction(
                        qualifiedForRescheduling.map { it.taskId }, ReplyType.SNOOZE
                    )
                )
            }

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