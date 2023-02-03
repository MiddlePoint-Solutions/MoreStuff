package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.InitAction
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsMiddleware(
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
            is InitAction -> scope.launch {
                val settings = userRepository.getUserSettings(AppSettings())
                dispatch(SettingAction.LoadSettings(settings))
            }

            is SettingAction.SetSnoozeLimit -> scope.launch {
                userRepository.setSnoozeLimit(action.limit)
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}