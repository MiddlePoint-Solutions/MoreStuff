package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.redux.state.UserAction
import co.softov.morestuff.android.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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