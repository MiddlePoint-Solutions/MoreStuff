package io.middlepoint.morestuff.shared.domain.redux.middleware

import io.middlepoint.morestuff.shared.domain.enums.PriorityActionType
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.middleware.PriorityAction.*
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.usecase.priority.UpdateTaskReviewPriorityUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdatePlannedTasksPriorityUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTaskPriorityScoreUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class PriorityAction : Action.FeatureAction() {

    data class UndoTaskPriorityUpdateAction(
        val taskId: Long,
        val score: Long,
    ) : PriorityAction()

    data class TaskPriorityUpdateAction(
        val task: Long,
        val actionType: PriorityActionType,
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