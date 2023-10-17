package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction.*
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.priority.UpdateTaskReviewPriorityUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdatePlannedTasksPriorityUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskPriorityScoreUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class PriorityAction : Action.FeatureAction() {

    data class UndoTaskPriorityUpdateAction(
        val taskId: Long,
        val score: Long
    ) : PriorityAction()

    data class TaskPriorityUpdateAction(
        val task: Long,
        val actionType: PriorityActionType
    ) : PriorityAction()

    data object UpdatePlannedPriorityAction : PriorityAction()

}

class PriorityMiddleware(
    private val updateTaskReviewPriorityUseCase: UpdateTaskReviewPriorityUseCase,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
    private val updatePlannedTasksPriorityUseCase: UpdatePlannedTasksPriorityUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is TaskPriorityUpdateAction -> scope.launch {
                updateTaskReviewPriorityUseCase(action.task, action.actionType)
            }

            is UndoTaskPriorityUpdateAction -> scope.launch {
                updateTaskPriorityScoreUseCase(action.taskId, action.score)
            }

            is UpdatePlannedPriorityAction -> scope.launch {
                updatePlannedTasksPriorityUseCase()
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}