package co.softov.morestuff.android.domain.redux.middleware

import arrow.core.Either
import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.ReplyType.DONE
import co.softov.morestuff.android.domain.enums.ReplyType.LATER
import co.softov.morestuff.android.domain.enums.ReplyType.SNOOZE
import co.softov.morestuff.android.domain.enums.ReplyType.TOMORROW
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Priority.Later
import co.softov.morestuff.android.domain.model.Priority.Now
import co.softov.morestuff.android.domain.model.Priority.Plan
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.dailySnoozeLimit
import co.softov.morestuff.android.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ExecuteScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.RescheduleTaskAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.RescheduleTasksAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ScheduleCreatedAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ScheduleReplyAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.SmartRescheduleAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.schedule.CancelActiveScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.CreateScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTaskScheduleCountUseCase
import co.softov.morestuff.android.domain.usecase.schedule.RescheduleTaskUseCase
import co.softov.morestuff.android.domain.usecase.schedule.RescheduleTaskUseCaseParams
import co.softov.morestuff.android.domain.usecase.schedule.ScheduleAtTimeUseCase
import co.softov.morestuff.android.domain.usecase.schedule.ScheduleReviewNotificationsUseCase
import co.softov.morestuff.android.domain.usecase.schedule.SetScheduleFulfilledUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class ScheduleAction : Action.FeatureAction() {
    data class ExecuteScheduleAction(val scheduleId: Long) : ScheduleAction()
    data class RescheduleTaskAction(val taskId: Long, val priority: Priority) : ScheduleAction()
    data class CancelActiveScheduleAction(val taskId: Long) : ScheduleAction()

    data class RescheduleTasksAction(
        val taskIds: List<Long>,
        val priority: Priority,
    ) : ScheduleAction()

    internal data class ScheduleCreatedAction(val schedule: ScheduleDomain) : ScheduleAction()

    internal data class ScheduleReplyAction(
        val schedule: ScheduleDomain,
        val replyType: ReplyType,
    ) : ScheduleAction()

    internal data class SmartRescheduleAction(
        val taskIds: List<Long>,
        val replyType: ReplyType,
    ) : ScheduleAction()
}

class ScheduleMiddleware(
    private val scheduleAtTimeUseCase: ScheduleAtTimeUseCase,
    private val scheduleReviewNotificationsUseCase: ScheduleReviewNotificationsUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val getTaskScheduleCountUseCase: GetTaskScheduleCountUseCase,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
    private val setScheduleFulfilledUseCase: SetScheduleFulfilledUseCase,
    private val rescheduleTaskUseCase: RescheduleTaskUseCase,
) : Middleware<AppState> {
    val timeManager: TimeManager = TimeManagerImpl()
    private val oneHourLater = timeManager.todayLocalDateTimeByAdding(hour = 1, minute = 0)
    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {

            is SettingAction.InitSettings -> {
                if (action.firstTime) {
                    scope.launch { scheduleReviewNotificationsUseCase() }
                }
            }

            is TaskAction.TaskCreatedAction -> scope.launch {
                with(action) {
                    createScheduleUseCase(task.id, priority).map {
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
                    val params = RescheduleTaskUseCaseParams(taskId, priority)
                    rescheduleTaskUseCase(params).map {
                        dispatch(ScheduleCreatedAction(it))
                    }
                }
            }

            is ScheduleAction.CancelActiveScheduleAction -> scope.launch {
                with(action) {
                    cancelActiveScheduleUseCase(taskId)
                }
            }

            is RescheduleTasksAction -> scope.launch {
                with(action) {
                    action.taskIds.forEach {
                        val params = RescheduleTaskUseCaseParams(it, priority)
                        rescheduleTaskUseCase(params).map { schedule ->
                            dispatch(ScheduleCreatedAction(schedule))
                        }
                    }
                }
            }

            is ScheduleReplyAction -> {
                val replyType = action.replyType
                val schedule = action.schedule
                when (replyType) {
                    LATER -> dispatch(RescheduleTaskAction(schedule.taskId, Plan(TODO(""))))
                    SNOOZE -> dispatch(RescheduleTaskAction(schedule.taskId, Plan(oneHourLater.toString())))
                    TOMORROW -> dispatch(RescheduleTaskAction(schedule.taskId, Later()))
                    DONE -> dispatch(TaskAction.CompleteTaskAction(schedule.taskId, true))
                }
            }

            is SmartRescheduleAction -> {
                when (action.replyType) {
                    LATER -> TODO()
                    SNOOZE -> action.taskIds.forEach { taskId ->
                        if (state.dailySnoozeLimit > 0) {
                            checkTaskSnoozeLimit(scope, taskId, state.dailySnoozeLimit, dispatch)
                        } else {
                            dispatch(RescheduleTaskAction(taskId, Now()))
                        }
                    }

                    TOMORROW -> TODO()
                    DONE -> TODO()
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

            else -> NoOp
        }

        return next(state, action, dispatch)
    }

    private fun checkTaskSnoozeLimit(
        scope: CoroutineScope,
        taskId: Long,
        snoozeLimit: Int,
        dispatch: Dispatch,
    ) {
        scope.launch {
            val timeOption = when (val result = getTaskScheduleCountUseCase(taskId)) {
                is Either.Right -> {
                    Timber.d("### Today Schedule count, taskId: $taskId = ${result.value} ###")
                    if (result.value > snoozeLimit) Later() else Now()
                }

                else -> Now()
            }
            dispatch(RescheduleTaskAction(taskId, timeOption))
        }
    }
}