package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.redux.state.PriorityAction.SetPriority
import kotlinx.coroutines.CoroutineScope

data class PriorityState(
    val current: Priority = Priority.Today(),
    val options: List<PriorityOption> = listOf()
)

sealed class PriorityAction : Action.FeatureAction() {
    data class SetPriority(val priority: Priority) : PriorityAction()
}

fun AppState.reducePriorityState(action: Action): AppState {
    return when (action) {
        is Init,
        is PriorityAction -> copy(priorityState = priorityState.reduce(action))
        else -> this
    }
}

fun PriorityState.reduce(action: Action): PriorityState {
    return when (action) {
        is SetPriority -> copy(current = action.priority)
        else -> this
    }
}

class PriorityMiddleware(
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}



