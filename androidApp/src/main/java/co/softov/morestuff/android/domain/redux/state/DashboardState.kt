package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class DashboardState(
    val currentPriority: Priority = Priority.Today(),
    val taskSnoozeLimit: Int = 3
)

sealed class DashboardAction : Action.FeatureAction() {



}

fun AppState.reduceDashboardState(action: Action): AppState {
    return when (action) {
        is Init,
        is DashboardAction -> copy(userState = userState.reduce(action))
        else -> this
    }
}

fun DashboardState.reduce(action: Action): DashboardState {
    return when (action) {

        else -> this
    }
}



