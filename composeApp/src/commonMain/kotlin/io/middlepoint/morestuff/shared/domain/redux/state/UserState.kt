package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.enums.Status
import io.middlepoint.morestuff.shared.domain.model.User
import io.middlepoint.morestuff.shared.domain.redux.action.UserAction
import io.middlepoint.morestuff.shared.domain.redux.action.UserAction.*
import io.middlepoint.morestuff.shared.domain.redux.store.Action

data class UserState(
  val status: Status = Status.Loading,
  val user: User? = null,
  val isOldUser: Boolean = false,
)

fun AppState.reduceUserState(action: Action): AppState {
  return when (action) {
    is UserAction -> copy(userState = userState.reduce(action))
    else -> this
  }
}

fun UserState.reduce(action: UserAction): UserState {
  return when (action) {
    is Authenticated -> copy(
      status = Status.Ready,
      user = action.user,
    )

    is NotAuthenticated -> copy(
      status = Status.Ready,
      user = null
    )

    is SetOldUser -> copy(
      isOldUser = action.isOldUser
    )

    SignOut -> this
  }
}

