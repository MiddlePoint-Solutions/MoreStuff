package io.middlepoint.morestuff.shared.domain.redux.middleware

import io.middlepoint.morestuff.shared.data.service.TimeManagerImpl
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType.DONE
import io.middlepoint.morestuff.shared.domain.enums.ReplyType.LATER
import io.middlepoint.morestuff.shared.domain.enums.ReplyType.SNOOZE
import io.middlepoint.morestuff.shared.domain.enums.ReplyType.TOMORROW
import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScheduleAction.*
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScheduleAction.ScheduleCreatedAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScheduleAction.ScheduleReplyAction
import io.middlepoint.morestuff.shared.domain.redux.state.SettingAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CancelActiveScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateOneTimeScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateReminderUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ToggleQuickReminderUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleAtTimeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleWorkUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.SetScheduleFulfilledUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCase
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
    data class ToggleReminderScheduleAction(val taskId: Long) : ScheduleAction()
    data class CreateReminderScheduleAction(val taskId: Long) : ScheduleAction()
    data class CancelScheduleAction(
        val taskIds: List<Long>,
        val scheduleType: List<ScheduleType> = ScheduleType.entries.toList()
    ) : ScheduleAction()

    internal data class ScheduleCreatedAction(val schedule: ScheduleDomain, val task: TaskDomain) : ScheduleAction()

    internal data class ScheduleReplyAction(
      val schedule: ScheduleDomain,
      val replyType: ReplyType,
    ) : ScheduleAction()

}

class ScheduleMiddleware(
    private val scheduleAtTimeUseCase: ScheduleAtTimeUseCase,
    private val scheduleWorkUseCase: ScheduleWorkUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val createOneTimeScheduleUseCase: CreateOneTimeScheduleUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val toggleQuickReminderUseCase: ToggleQuickReminderUseCase,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
    private val setScheduleFulfilledUseCase: SetScheduleFulfilledUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : Middleware<AppState> {

    val timeManager: TimeManager = TimeManagerImpl()

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {

            is SettingAction.InitSettings -> scope.launch {
                scheduleWorkUseCase(/*action.settings*/)
            }

            is TaskAction.TaskCreatedAction -> scope.launch {
                with(action) {
                    createOneTimeScheduleUseCase(task.id, priority).map {
                        dispatch(ScheduleCreatedAction(it, task))
                    }
                }
            }

            is TaskAction.CompleteTasksAction -> scope.launch {
                if (action.complete) {
                    dispatch(CancelScheduleAction(action.taskIds))
                }
            }

            is RescheduleTaskAction -> scope.launch {
                with(action) {
                    getTaskUseCase(taskId).map { task ->
                        createScheduleUseCase(taskId, scheduleType, localDateTime).map { schedule ->
                            dispatch(ScheduleCreatedAction(schedule, task))
                        }
                    }
                }
            }

            is CancelActiveScheduleAction -> scope.launch {
                cancelActiveScheduleUseCase(listOf(action.taskId), listOf(ScheduleType.OneTime))
            }

            // TODO: create use-case for this action
            is ScheduleReplyAction -> with(action) {
                when (replyType) {
                    LATER -> {}
                    TOMORROW -> {
                        val tomorrowTime = timeManager.tomorrowLocalDateTime(12)
                        dispatch(
                            RescheduleTaskAction(
                                schedule.taskId,
                                schedule.scheduleType,
                                tomorrowTime
                            )
                        )
                    }

                    SNOOZE -> {
                        val snoozeTime =
                            timeManager.todayLocalDateTimeByAdding(hour = 1, minute = 0)
                        dispatch(
                            RescheduleTaskAction(
                                schedule.taskId,
                                schedule.scheduleType,
                                snoozeTime
                            )
                        )
                    }

                    DONE -> {
                        dispatch(
                            TaskAction.CompleteTasksAction(listOf(schedule.taskId), true)
                        )
                    }
                }
            }

            is ScheduleCreatedAction -> {
                scope.launch {
                    action.schedule.scheduleLocalTime?.let { time ->
                        scheduleAtTimeUseCase(action.schedule.id, time, action.task.title, action.task.id)
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

            is ToggleReminderScheduleAction -> scope.launch {
               /* toggleQuickReminderUseCase(action.taskId).map { //TODO: fix this later
                    dispatch(ScheduleCreatedAction(it))
                }*/
            }

            is CreateReminderScheduleAction -> scope.launch {
               /* createReminderUseCase(action.taskId).map { //TODO: fix this later
                    dispatch(ScheduleCreatedAction(it))
                }*/
            }

            is CancelScheduleAction -> scope.launch {
                cancelActiveScheduleUseCase(action.taskIds, action.scheduleType)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}