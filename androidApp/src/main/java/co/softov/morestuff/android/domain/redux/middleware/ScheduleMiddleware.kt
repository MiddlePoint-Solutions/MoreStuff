package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.ReplyType.DONE
import co.softov.morestuff.android.domain.enums.ReplyType.LATER
import co.softov.morestuff.android.domain.enums.ReplyType.SNOOZE
import co.softov.morestuff.android.domain.enums.ReplyType.TOMORROW
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ExecuteScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.RescheduleTaskAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ScheduleCreatedAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ScheduleReplyAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.schedule.CancelActiveScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.CreateOneTimeScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.CreateReminderScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.CreateScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.ScheduleAtTimeUseCase
import co.softov.morestuff.android.domain.usecase.schedule.ScheduleNotificationsAndWorkUseCase
import co.softov.morestuff.android.domain.usecase.schedule.SetScheduleFulfilledUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

sealed class ScheduleAction : Action.FeatureAction() {
    data class ExecuteScheduleAction(val scheduleId: Long) : ScheduleAction()
    data class RescheduleTaskAction(
        val taskId: Long,
        val scheduleType: ScheduleType,
        val localDateTime: LocalDateTime
    ) : ScheduleAction()

    data class CancelActiveScheduleAction(val taskId: Long) : ScheduleAction()

    data class CreateReminderScheduleAction(val taskId: Long) : ScheduleAction()
    data class CancelReminderScheduleAction(val taskId: Long) : ScheduleAction()

    internal data class ScheduleCreatedAction(val schedule: ScheduleDomain) : ScheduleAction()

    internal data class ScheduleReplyAction(
        val schedule: ScheduleDomain,
        val replyType: ReplyType,
    ) : ScheduleAction()

}

class ScheduleMiddleware(
    private val scheduleAtTimeUseCase: ScheduleAtTimeUseCase,
    private val scheduleNotificationsAndWorkUseCase: ScheduleNotificationsAndWorkUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val createOneTimeScheduleUseCase: CreateOneTimeScheduleUseCase,
    private val createReminderScheduleUseCase: CreateReminderScheduleUseCase,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
    private val setScheduleFulfilledUseCase: SetScheduleFulfilledUseCase,
) : Middleware<AppState> {
    val timeManager: TimeManager = TimeManagerImpl()
    private val oneHourLater get() = timeManager.todayLocalDateTimeByAdding(hour = 1, minute = 0)
    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {

            is OnResumeAction,
            is NotificationAction.ShowReviewNotification,
            is SettingAction.InitSettings -> {
                scheduleNotificationsAndWorkUseCase()
            }

            is TaskAction.TaskCreatedAction -> scope.launch {
                with(action) {
                    createOneTimeScheduleUseCase(task.id, priority).map {
                        dispatch(ScheduleCreatedAction(it))
                    }
                }
            }

            is TaskAction.CompleteTaskAction -> scope.launch {
                if (action.complete) {
                    cancelActiveScheduleUseCase(action.taskId)
                }
            }

            is TaskAction.CompleteTasksAction -> scope.launch {
                if (action.complete) {
                    action.taskIds.forEach {
                        cancelActiveScheduleUseCase(it)
                    }
                }
            }

            is RescheduleTaskAction -> scope.launch {
                with(action) {
                    createScheduleUseCase(taskId, scheduleType, localDateTime).map {
                        dispatch(ScheduleCreatedAction(it))
                    }
                }
            }

            is ScheduleAction.CancelActiveScheduleAction -> scope.launch {
                with(action) {
                    cancelActiveScheduleUseCase(taskId)
                }
            }

            is ScheduleReplyAction -> {
                val replyType = action.replyType
                val schedule = action.schedule
                when (replyType) {
                    LATER -> {}
                    TOMORROW -> {}
                    SNOOZE -> dispatch(
                        RescheduleTaskAction(schedule.taskId, schedule.scheduleType, oneHourLater)
                    )

                    DONE -> dispatch(TaskAction.CompleteTaskAction(schedule.taskId, true))
                }
            }

            is ScheduleCreatedAction -> {
                scope.launch {
                    action.schedule.scheduleLocalTime?.let { time ->
                        scheduleAtTimeUseCase(action.schedule.id, time)
                    }
                }
            }

            is ExecuteScheduleAction -> scope.launch {
                getScheduleUseCase(action.scheduleId).map { schedule ->
                    if (schedule.active) {
                        setScheduleFulfilledUseCase(schedule.id)
                        dispatch(CreateScheduleMessageAction(schedule.id))
                    }
                }
            }

            is ScheduleAction.CreateReminderScheduleAction -> scope.launch {
                createReminderScheduleUseCase(action.taskId).map {
                    dispatch(ScheduleCreatedAction(it))
                }
            }

            is ScheduleAction.CancelReminderScheduleAction -> scope.launch {
                cancelActiveScheduleUseCase(action.taskId)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}