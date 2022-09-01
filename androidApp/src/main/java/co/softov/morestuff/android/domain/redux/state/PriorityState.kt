package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.redux.middleware.ErrorAction
import co.softov.morestuff.android.domain.redux.state.PriorityAction.*
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsParams
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class PriorityState(
    val current: Priority = Priority.Today(),
    val options: List<PriorityOption> = listOf()
)

sealed class PriorityAction : Action.FeatureAction() {
    data class SetPriority(val priority: Priority) : PriorityAction()
    data class SetPriorityOptions(
        val priority: Priority,
        val options: List<PriorityOption>
    ) : PriorityAction()

    data class ChangePriorityOption(val option: PriorityOption) : PriorityAction()
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
        is SetPriorityOptions -> copy(options = action.options)
        is ChangePriorityOption -> reduceChangePriorityOption(current, action)
        else -> this
    }
}

private fun PriorityState.reduceChangePriorityOption(
    current: Priority,
    action: ChangePriorityOption
) = copy(
    current = when (current) {
        is Priority.Later -> current.copy(option = action.option)
        is Priority.Today -> current.copy(option = action.option)
        is Priority.Tomorrow -> current.copy(option = action.option)
    }
)

class PriorityMiddleware(
    private val getPriorityOptionsUseCase: GetPriorityOptionsUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is SetPriority -> scope.launch {
                val params = GetPriorityOptionsParams(action.priority)
                getPriorityOptionsUseCase(params).fold(
                    ifLeft = {
                        dispatch(ErrorAction(it))
                    },
                    ifRight = {
                        dispatch(SetPriorityOptions(priority = it.priority, options = it.options))
                    }
                )

            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}



