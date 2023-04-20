package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.state.SettingAction.InitSettings
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.InitStoreAction
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsMiddleware(
    private val checkFirstTimeUseCase: CheckFirstTimeUseCase,
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
            is InitStoreAction -> scope.launch {
                val firstTime = checkFirstTimeUseCase()
                val settings = getUserSettingsUseCase()
                dispatch(InitSettings(firstTime, settings))
            }

            is SettingAction.SetSnoozeLimit -> scope.launch {
                val updatedSettings = AppSettings(snoozeLimit = action.limit)
                saveUserSettings(updatedSettings)
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}