package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Setting
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.InitAction
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.repository.UserRepository
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsMiddleware(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val saveUserSettings: SaveUserSettings

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
                val settings = getUserSettingsUseCase()
                dispatch(SettingAction.LoadSettings(settings))
            }

            is SettingAction.SetSnoozeLimit -> scope.launch {
                val updatedSettings = AppSettings(Setting.SnoozeLimit(action.limit))
                saveUserSettings(updatedSettings)
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}