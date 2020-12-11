package co.softov.morestuff.androidApp.domain.redux.state

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.UserSettings
import co.softov.morestuff.androidApp.domain.redux.*
import co.softov.morestuff.androidApp.domain.redux.state.UserAction.*
import co.softov.morestuff.androidApp.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class UserState(
    val currentPriority: Priority = Priority.Today(),
    val taskSnoozeLimit: Int = 3
)

sealed class UserAction : Action.FeatureAction() {

    data class SetUserSettings(val settings: UserSettings) : UserAction()
    data class ChangeSnoozeLimit(val limit: Int) : UserAction()

}

fun AppState.reduceUserState(action: Action): AppState {
    return when (action) {
        is Init,
        is UserAction -> copy(userState = userState.reduce(action))
        else -> this
    }
}

fun UserState.reduce(action: Action): UserState {
    return when (action) {
        is ChangeSnoozeLimit -> copy(taskSnoozeLimit = action.limit)
        is SetUserSettings -> copy(
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
                dispatch(SetUserSettings(settings))
            }

            is ChangeSnoozeLimit -> scope.launch {
                userRepository.setSnoozeLimit(action.limit)
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }

}



