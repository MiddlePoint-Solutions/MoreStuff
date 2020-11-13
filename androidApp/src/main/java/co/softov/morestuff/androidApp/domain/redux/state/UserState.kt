package co.softov.morestuff.androidApp.domain.redux.state

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState

data class UserState(
    val currentPriority: Priority = Priority.Today()
)

sealed class UserAction : Action.FeatureAction() {

}

fun AppState.reduceSignInState(action: Action): AppState {
    return when (action) {
        else -> this
    }
}

fun UserState.reduce(action: Action): UserState {
    return when (action) {
        else -> this
    }
}



