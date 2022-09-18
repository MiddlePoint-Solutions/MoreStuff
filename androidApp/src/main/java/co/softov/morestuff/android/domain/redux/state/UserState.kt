package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.model.UserSettings
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.InitAction

data class UserState(
    val taskSnoozeLimit: Int = 3
)

sealed class UserAction : Action.FeatureAction() {

    data class SetUserSettings(val settings: UserSettings) : PriorityAction()
    data class ChangeSnoozeLimit(val limit: Int) : PriorityAction()

}

fun AppState.reduceUserState(action: Action): AppState {
    return when (action) {
        is InitAction,
        is PriorityAction -> copy(user = user.reduce(action))
        else -> this
    }
}

fun UserState.reduce(action: Action): UserState {
    return when (action) {
        is UserAction.ChangeSnoozeLimit -> copy(taskSnoozeLimit = action.limit)
        is UserAction.SetUserSettings -> copy(
            taskSnoozeLimit = action.settings.snoozeLimit
        )
        else -> this
    }
}



