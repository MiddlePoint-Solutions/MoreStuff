package co.softov.morestuff.android.domain.redux.middleware

import arrow.core.Either
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.Priority.*
import co.softov.morestuff.android.domain.enums.ReplyType.*
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.android.domain.redux.middleware.ResponseAction.ScheduleReplyAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.*
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.TaskComplete
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.android.domain.usecase.schedule.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class ScheduleAction : Action.FeatureAction() {
    data class ExecuteScheduleAction(val scheduleId: Long) : ScheduleAction()
    data class RescheduleTaskAction(val taskId: Long, val priority: Priority) : ScheduleAction()

    internal data class ScheduleCreatedAction(val schedule: Schedule) : ScheduleAction()
}

class ScheduleMiddleware(
    private val scheduleAtTimeUseCase: ScheduleAtTimeUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val getTaskScheduleCountUseCase: GetTaskScheduleCountUseCase,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
    private val setScheduleFulfilledUseCase: SetScheduleFulfilledUseCase
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
                createScheduleUseCase(action.task.id, action.priority).map {
                    dispatch(ScheduleCreatedAction(it))
                }
            }

            is TaskComplete -> scope.launch {
                cancelActiveScheduleUseCase(action.taskId)
            }

            is RescheduleTaskAction -> scope.launch {
                cancelActiveScheduleUseCase(action.taskId)
                createScheduleUseCase(action.taskId, action.priority).map {
                    dispatch(ScheduleCreatedAction(it))
                }
            }

            is ScheduleReplyAction -> {
                val replyType = action.replyType
                val schedule = action.schedule
                when (replyType) {
                    LATER -> dispatch(RescheduleTaskAction(schedule.taskId, Later()))
                    SNOOZE -> if (state.snoozeLimit > 0) {
                        checkTodayScheduleReply(scope, schedule, state.snoozeLimit, dispatch)
                    } else {
                        dispatch(RescheduleTaskAction(schedule.taskId, Today()))
                    }
                    TOMORROW -> dispatch(RescheduleTaskAction(schedule.taskId, Tomorrow()))
                    DONE -> dispatch(TaskComplete(schedule.taskId))
                }
            }

            is ScheduleCreatedAction -> {
                scope.launch {
                    action.schedule.scheduleTime?.let { time ->
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

    private fun checkTodayScheduleReply(
        scope: CoroutineScope,
        schedule: Schedule,
        snoozeLimit: Int,
        dispatch: Dispatch
    ) {
        scope.launch {
            val timeOption =
                when (val result = getTaskScheduleCountUseCase(schedule.taskId)) {
                    is Either.Right -> {
                        Timber.d("### Today Schedule count, taskId: ${schedule.taskId} = ${result.value} ###")
                        if (result.value > snoozeLimit) Tomorrow() else Today()
                    }
                    else -> Today()
                }
            dispatch(RescheduleTaskAction(schedule.taskId, timeOption))
        }
    }
}