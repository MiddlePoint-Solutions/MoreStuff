package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.model.User
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Action

data class UserState(
    val user: User? = null
)

sealed class UserAction : Action.FeatureAction() {
    data class InitSettings(val settings: AppSettings) : UserAction()
}

fun AppState.reduceUserState(action: Action): AppState {
    return when (action) {
        is UserAction -> copy(userState = userState.reduce(action))
        else -> this
    }
}

fun UserState.reduce(action: UserAction): UserState {
    return when (action) {
        else -> this
    }
}

