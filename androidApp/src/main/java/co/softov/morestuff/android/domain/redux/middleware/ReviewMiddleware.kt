package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.state.ReviewAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.redux.store.Test
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class ReviewMiddleware(
    private val getSchedulesWithTitleList: GetSchedulesWithTitleList
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is ReviewAction.InitReview -> {
                scope.launch {
                    dispatch(ReviewAction.SetSchedulesForReview(getSchedulesWithTitleList()))
                }
            }

            is ReviewAction.SetSchedulesForReview -> {
                scope.launch {
                    delay(5000)
                    dispatch(Test("${state.reviewState}"))

                }
            }
            is Test -> {
                Timber.d("ReviewState: ${state.reviewState}")
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}