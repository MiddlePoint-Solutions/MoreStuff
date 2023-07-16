package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.SettingAction.*
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.InitStoreAction

data class AppSettingsState(
    val isFirstTime: Boolean = false,
    val appTheme: AppTheme = AppTheme.MODE_AUTO,
    val settings: AppSettings = AppSettings()
)

sealed class SettingAction : Action.FeatureAction() {
    data class InitSettings(val firstTime: Boolean, val settings: AppSettings) : SettingAction()
    data class SetSnoozeLimit(val limit: Int) : SettingAction()
    data class EnableSmartReminder(val enable: Boolean) : SettingAction()

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
        is InitSettings -> with(action) {
            copy(
                isFirstTime = isFirstTime,
                settings = settings
            )
        }

        is SetSnoozeLimit -> copy(settings = settings.copy(snoozeLimit = action.limit))
        is EnableSmartReminder -> copy(settings = settings.copy(smartReminderEnabled = action.enable))

        is SetAppTheme -> copy(appTheme = action.theme)

        else -> this
    }
}



