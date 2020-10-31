package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.ScheduleAction.ScheduleCreatedAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.androidApp.domain.usecase.schedule.CreateScheduleUseCase
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ScheduleAction : Action.FeatureAction() {
    data class ScheduleCreatedAction(val schedule: Schedule) : ScheduleAction()
}

class ScheduleMiddleware(
    private val createScheduleUseCase: CreateScheduleUseCase
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
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}