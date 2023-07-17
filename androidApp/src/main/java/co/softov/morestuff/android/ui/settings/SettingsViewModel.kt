package co.softov.morestuff.android.ui.settings

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.DevAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val devTools: DevTools,
) : NoStateViewModel() {

    val model = MutableStateFlow(
        with(store.state.value.settingState) {
            SettingsViewState(
                appThemeIndex = appTheme.ordinal,
                snoozeLimit = snoozeLimit,
            )
        }
    )

    init {
        loadData()
    }

    override fun onAppStateChange(state: AppState) {
        model.update {
            it.copy(
                appThemeIndex = state.settingState.appTheme.ordinal,
                snoozeLimit = state.settingState.snoozeLimit,
            )
        }
    }

    fun onSnoozeLimitChanged(limit: Float) {
        dispatchAppStoreAction(SettingAction.SetSnoozeLimit(limit.toInt()))
    }

    fun clearPendingMessages() {
        dispatchAppStoreAction(DevAction.ClearActiveReminderMessages)
    }

    fun testReviewNotification() {
        devTools.testReviewNotification()
    }

    fun selectAppTheme(index: Int) {
        dispatchAppStoreAction(SettingAction.SetAppTheme(AppTheme[index]))
    }
}