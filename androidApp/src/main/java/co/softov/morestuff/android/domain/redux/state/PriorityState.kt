package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.redux.state.PriorityAction.*
import co.softov.morestuff.android.domain.redux.store.Action

data class PriorityState(
    val current: Priority = Priority.Now(),
    val options: List<PriorityOption> = listOf()
)

sealed class PriorityAction : Action.FeatureAction() {
    data class SetPriority(val priority: Priority) : PriorityAction()
    data class SetPriorityOptions(
        val priority: Priority,
        val options: List<PriorityOption>
    ) : PriorityAction()

    data class SetCurrentPriorityOption(val option: PriorityOption) : PriorityAction()
}

fun AppState.reducePriorityState(action: Action): AppState {
    return when (action) {
        is PriorityAction -> copy(priorityState = priorityState.reduce(action))
        else -> this
    }
}

fun PriorityState.reduce(action: Action): PriorityState {
    return when (action) {
        is SetPriorityOptions -> copy(current = action.priority, options = action.options)
        is SetCurrentPriorityOption -> reduceChangePriorityOption(current, action)
        else -> this
    }
}

private fun PriorityState.reduceChangePriorityOption(
    current: Priority,
    action: SetCurrentPriorityOption
) = copy(
    current = when (current) {
        is Priority.Plan -> current.copy("")
        is Priority.Now -> current.copy(option = action.option)
        is Priority.Later -> current.copy(option = action.option)
    }
)



