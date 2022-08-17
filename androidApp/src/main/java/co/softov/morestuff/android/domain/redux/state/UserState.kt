package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.model.UserSettings
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class UserState(
    val currentPriority: Priority = Priority.Today(),
    val taskSnoozeLimit: Int = 3
)

sealed class UserAction : Action.FeatureAction() {

    data class SetUserSettings(val settings: UserSettings) : DashboardAction()
    data class ChangeSnoozeLimit(val limit: Int) : DashboardAction()

}

fun AppState.reduceUserState(action: Action): AppState {
    return when (action) {
        is Init,
        is DashboardAction -> copy(userState = userState.reduce(action))
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

class UserMiddleware(
    private val userRepository: UserRepository
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is Init -> scope.launch {
                val settings = userRepository.getUserSettings()
                dispatch(UserAction.SetUserSettings(settings))
            }

            is UserAction.ChangeSnoozeLimit -> scope.launch {
                userRepository.setSnoozeLimit(action.limit)
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }

}



