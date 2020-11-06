package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.Scheduler
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.ScheduleAction.*
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCompleteAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.androidApp.domain.usecase.schedule.*
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class ScheduleAction : Action.FeatureAction() {
    data class ExecuteScheduleAction(val scheduleId: Long) : ScheduleAction()
    data class RescheduleAction(val taskId: Long, val priority: Priority) : ScheduleAction()

    internal data class ScheduleCreatedAction(val schedule: Schedule) : ScheduleAction()
    internal data class ScheduleCanceledAction(val schedule: Schedule) : ScheduleAction()
}

class ScheduleMiddleware(
    private val scheduler: Scheduler,
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
    private val rescheduleUseCase: RescheduleUseCase,
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

            is TaskCompleteAction -> scope.launch {
                cancelActiveScheduleUseCase(action.taskId).map { schedule ->
                    dispatch(ScheduleCanceledAction(schedule))
                }
            }

            is RescheduleAction -> scope.launch {
                rescheduleUseCase(action.taskId, action.priority).map {
                    dispatch(ScheduleCreatedAction(it))
                }
            }

            is ScheduleCreatedAction -> {
                action.schedule.apply {
                    scheduleTime?.let { time -> scheduler.scheduleAtExact(id, time) }
                }
            }

            is ScheduleCanceledAction -> {
                scheduler.cancelSchedule(action.schedule.id)
            }

            is ExecuteScheduleAction -> scope.launch {
                setScheduleFulfilledUseCase(action.scheduleId)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}