package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.model.Defaults
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.SettingAction.*
import co.softov.morestuff.android.domain.redux.store.Action

data class AppSettings(
    val isFirstTime: Boolean = true,
    val appTheme: AppTheme = AppTheme.System,
    val snoozeLimit: Int = Defaults.DEFAULT_SNOOZE_LIMIT,
    val enableConfetti: Boolean = true,
)

sealed class SettingAction : Action.FeatureAction() {
    data class InitSettings(val settings: AppSettings) : SettingAction()
    data class SetSnoozeLimit(val amount: Int) : SettingAction()
    data class SetAppTheme(val theme: AppTheme) : SettingAction()
    data class EnableConfetti(val enable: Boolean) : SettingAction()



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
    }
}
