package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.state.ReviewAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import kotlinx.coroutines.CoroutineScope

class ReviewMiddleware : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is ReviewAction.ScheduleReviewResults -> {
                with(action) {
                    dispatch(ScheduleAction.RescheduleTasksAction(tomorrow, Priority.Later()))
                    dispatch(ScheduleAction.RescheduleTasksAction(this.low, Priority.Now()))
                    dispatch(ScheduleAction.RescheduleTasksAction(this.high, Priority.Now()))
                    dispatch(TaskAction.CompleteTasksAction(done, true))
                }
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}