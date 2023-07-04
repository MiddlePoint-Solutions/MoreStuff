package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction.TaskPriorityUpdateAction
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction.UndoTaskPriorityUpdateAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.task.DecrementTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetDefaultPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.IncrementTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskPriorityScoreUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class PriorityAction : Action.FeatureAction() {

    data class UndoTaskPriorityUpdateAction(
        val taskId: Long,
        val score: Long
    ) : PriorityAction()

    data class TaskPriorityUpdateAction(
        val taskId: Long,
        val actionType: PriorityActionType
    ) : PriorityAction()

}

class PriorityMiddleware(
    private val getDefaultPriorityScoreUseCase: GetDefaultPriorityScoreUseCase,
    private val decrementTaskPriorityScoreUseCase: DecrementTaskPriorityScoreUseCase,
    private val incrementTaskPriorityScoreUseCase: IncrementTaskPriorityScoreUseCase,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
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
                when (action.actionType) {
                    PriorityActionType.Now -> {
                        val score = getDefaultPriorityScoreUseCase(Priority.Now())
                        updateTaskPriorityScoreUseCase(action.taskId, score)
                    }

                    PriorityActionType.Later -> {
                        val score = getDefaultPriorityScoreUseCase(Priority.Later())
                        updateTaskPriorityScoreUseCase(action.taskId, score)
                    }

                    PriorityActionType.More -> incrementTaskPriorityScoreUseCase(action.taskId)
                    PriorityActionType.Less -> decrementTaskPriorityScoreUseCase(action.taskId)

                }
            }

            is UndoTaskPriorityUpdateAction -> scope.launch {
                updateTaskPriorityScoreUseCase(action.taskId, action.score)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}