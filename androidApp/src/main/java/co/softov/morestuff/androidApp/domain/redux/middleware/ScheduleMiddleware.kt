package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.ScheduleAction.*
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCompleteAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.androidApp.domain.usecase.schedule.CancelActiveScheduleUseCase
import co.softov.morestuff.androidApp.domain.usecase.schedule.CreateScheduleUseCase
import co.softov.morestuff.androidApp.domain.usecase.schedule.HandleScheduleResponseUseCase
import co.softov.morestuff.androidApp.domain.usecase.schedule.RescheduleUseCase
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ScheduleAction : Action.FeatureAction() {
    data class ScheduleCreatedAction(val schedule: Schedule) : ScheduleAction()
    data class RescheduleAction(val taskId: Long, val priority: Priority) : ScheduleAction()
    data class ScheduleResponse(val scheduleId: Long, val replyType: ReplyType): ScheduleAction()
}

class ScheduleMiddleware(
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
    private val rescheduleUseCase: RescheduleUseCase,
    private val handleScheduleResponseUseCase: HandleScheduleResponseUseCase
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
                cancelActiveScheduleUseCase(action.taskId)
            }

            is RescheduleAction -> scope.launch {
                rescheduleUseCase(action.taskId, action.priority).map {
                    dispatch(ScheduleCreatedAction(it))
                }
            }

            is ScheduleResponse -> scope.launch {
                handleScheduleResponseUseCase(action.scheduleId, action.replyType)
            }


            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}