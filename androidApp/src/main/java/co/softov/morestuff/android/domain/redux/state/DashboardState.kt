package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.redux.*
import kotlinx.coroutines.CoroutineScope

data class DashboardState(
    val currentPriority: Priority = Priority.Today(),
)

sealed class DashboardAction : Action.FeatureAction() {

}

fun AppState.reduceDashboardState(action: Action): AppState {
    return when (action) {
        is Init,
        is DashboardAction -> copy(dashboard = dashboard.reduce(action))
        else -> this
    }
}

fun DashboardState.reduce(action: Action): DashboardState {
    return when (action) {

        else -> this
    }
}

class DashboardMiddleware(
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



