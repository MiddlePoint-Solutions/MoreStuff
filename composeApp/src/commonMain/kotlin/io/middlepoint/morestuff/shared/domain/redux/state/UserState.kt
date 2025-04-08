package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.model.User
import io.middlepoint.morestuff.shared.domain.redux.state.UserAction.*
import io.middlepoint.morestuff.shared.domain.redux.store.Action

data class UserState(
    val user: User? = null
)

sealed class UserAction : Action.FeatureAction() {
    data class Authenticated(val user: User) : UserAction()
    data class NotAuthenticated(val isSignOut: Boolean) : UserAction()
}

fun AppState.reduceUserState(action: Action): AppState {
    return when (action) {
        is UserAction -> copy(userState = userState.reduce(action))
        else -> this
    }
}

fun UserState.reduce(action: UserAction): UserState {
    return when (action) {
        is Authenticated -> copy(user = action.user)
        is NotAuthenticated -> copy(user = null)
    }
}

