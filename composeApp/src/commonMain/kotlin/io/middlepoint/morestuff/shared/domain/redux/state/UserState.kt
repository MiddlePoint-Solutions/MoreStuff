package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Action

data class UserState(
    val user: User? = null
)

sealed class UserAction : Action.FeatureAction() {
    data class InitSettings(val settings: AppSettings) : UserAction()
    data class EnableDevSettings(val enable: Boolean) : UserAction()
    data class SetSnoozeLimit(val amount: Int) : UserAction()
    data class SetAppTheme(val theme: AppTheme) : UserAction()
    data object OnBoardingComplete : UserAction()
    data class SetReviewTimeAction(val hour: Int, val minute: Int) : UserAction()
    data class EnableReviewHint(val enable: Boolean) : UserAction()
    data class SetVoiceLanguage(val language: Language) : UserAction()
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

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val createdAt: String,
    val lastLogin: String
)