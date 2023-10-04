package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.AppSetting.*
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.SettingAction.EnableConfetti
import co.softov.morestuff.android.domain.redux.state.SettingAction.InitSettings
import co.softov.morestuff.android.domain.redux.state.SettingAction.OnBoardingComplete
import co.softov.morestuff.android.domain.redux.state.SettingAction.SetAppTheme
import co.softov.morestuff.android.domain.redux.state.SettingAction.SetSnoozeLimit
import co.softov.morestuff.android.domain.redux.store.Action

data class AppSettings(
    val devSettings: Boolean = DevSettings.defaultValue,
    val isFirstTime: Boolean = FirstTime.defaultValue,
    val appTheme: AppTheme = AppTheme.valueOf(Theme.defaultValue),
    val snoozeLimit: Int = SnoozeLimit.defaultValue,
    val enableConfetti: Boolean = Confetti.defaultValue,
)

sealed class SettingAction : Action.FeatureAction() {
    data class InitSettings(val settings: AppSettings) : SettingAction()
    data class EnableDevSettings(val enable: Boolean) : SettingAction()
    data class SetSnoozeLimit(val amount: Int) : SettingAction()
    data class SetAppTheme(val theme: AppTheme) : SettingAction()
    data class EnableConfetti(val enable: Boolean) : SettingAction()
    data object OnBoardingComplete : SettingAction()

}

fun AppState.reduceSettingState(action: Action): AppState {
    return when (action) {
        is SettingAction -> copy(settings = settings.reduce(action))
        else -> this
    }
}

fun AppSettings.reduce(action: SettingAction): AppSettings {
    return when (action) {
        is InitSettings -> action.settings
        is SetSnoozeLimit -> copy(snoozeLimit = action.amount)
        is SetAppTheme -> copy(appTheme = action.theme)
        is EnableConfetti -> copy(enableConfetti = action.enable)
        OnBoardingComplete -> copy(isFirstTime = false)
        is SettingAction.EnableDevSettings -> copy(devSettings = action.enable)
    }
}
