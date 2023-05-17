package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.ReviewActionType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.ReviewAction.ReviewPriorityScoreUpdateAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.task.DecrementTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetDefaultPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.IncrementTaskPriorityScoreUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ReviewAction : Action.FeatureAction() {

    data class ReviewPriorityScoreUpdateAction(
        val taskId: Long,
        val actionType: ReviewActionType
    ) : ReviewAction()

}

class ReviewMiddleware(
    private val getDefaultPriorityScoreUseCase: GetDefaultPriorityScoreUseCase,
    private val decrementTaskPriorityScoreUseCase: DecrementTaskPriorityScoreUseCase,
    private val incrementTaskPriorityScoreUseCase: IncrementTaskPriorityScoreUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is ReviewPriorityScoreUpdateAction -> scope.launch {
                when (action.actionType) {
                    ReviewActionType.Now -> {
                        val score = getDefaultPriorityScoreUseCase(Priority.Now())
                    }

                    ReviewActionType.Later -> {
                        val score = getDefaultPriorityScoreUseCase(Priority.Later())

                    }

                    ReviewActionType.More -> incrementTaskPriorityScoreUseCase(action.taskId)
                    ReviewActionType.Less -> decrementTaskPriorityScoreUseCase(action.taskId)

                }
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}