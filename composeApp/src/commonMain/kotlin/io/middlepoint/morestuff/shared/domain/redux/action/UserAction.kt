package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.model.User
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class UserAction : Action.FeatureAction() {
  data class Authenticated(val user: User) : UserAction()
  data class NotAuthenticated(val isSignOut: Boolean) : UserAction()
  data class SetOldUser(val isOldUser: Boolean) : UserAction()
  data object SignOut : UserAction()
}