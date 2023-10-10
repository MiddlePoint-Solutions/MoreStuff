package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.SettingAction.EnableConfetti
import co.softov.morestuff.android.domain.redux.state.SettingAction.EnableDevSettings
import co.softov.morestuff.android.domain.redux.state.SettingAction.InitSettings
import co.softov.morestuff.android.domain.redux.state.SettingAction.OnBoardingComplete
import co.softov.morestuff.android.domain.redux.state.SettingAction.SetAppTheme
import co.softov.morestuff.android.domain.redux.state.SettingAction.SetSnoozeLimit
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.InitStoreAction
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.settings.GetAppSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsMiddleware(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveUserSettingUseCase: SaveUserSettingUseCase,

    ) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {
            is InitStoreAction -> scope.launch {
                dispatch(InitSettings(getAppSettingsUseCase()))
            }

            is EnableDevSettings -> scope.launch {
                saveUserSettingUseCase(AppSetting.DevSettings, action.enable)
            }

            is SetSnoozeLimit -> scope.launch {
                saveUserSettingUseCase(AppSetting.SnoozeLimit, action.amount)
            }

            is SetAppTheme -> scope.launch {
                saveUserSettingUseCase(AppSetting.Theme, action.theme.name)
            }

            is EnableConfetti -> scope.launch {
                saveUserSettingUseCase(AppSetting.Confetti, action.enable)
            }

            is OnBoardingComplete -> scope.launch {
                saveUserSettingUseCase(AppSetting.FirstTime, false)
            }
            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}