package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Defaults
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.SettingAction.*
import co.softov.morestuff.android.domain.redux.store.Action

data class AppSettingsState(
    val isFirstTime: Boolean = false,
    val appTheme: AppTheme = AppTheme.System,
    val snoozeLimit: Int = Defaults.DEFAULT_SNOOZE_LIMIT,
)

sealed class SettingAction : Action.FeatureAction() {
    data class InitSettings(val settings: AppSettings) : SettingAction()
    data class SetSnoozeLimit(val amount: Int) : SettingAction()
    data class SetAppTheme(val theme: AppTheme) : SettingAction()

}

fun AppState.reduceSettingState(action: Action): AppState {
    return when (action) {
        is SettingAction -> copy(settingState = settingState.reduce(action))
        else -> this
    }
}

fun AppSettingsState.reduce(action: Action): AppSettingsState {
    return when (action) {
        is InitSettings -> init(action.settings)
        is SetSnoozeLimit -> copy(snoozeLimit = action.amount)
        is SetAppTheme -> copy(appTheme = action.theme)
        else -> this
    }
}

private fun init(settings: AppSettings) =
    with(settings) {
        AppSettingsState(
            isFirstTime = isFirstTime,
            snoozeLimit = snoozeLimit,
            appTheme = appTheme,
        )
    }



