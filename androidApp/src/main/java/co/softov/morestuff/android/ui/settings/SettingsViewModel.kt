package co.softov.morestuff.android.ui.settings

import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.DevTools
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
        with(store.state.value.settings) {
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
                appThemeIndex = state.settings.appTheme.ordinal,
                snoozeLimit = state.settings.snoozeLimit,
                confettiEnabled = state.settings.enableConfetti
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

    fun enableConfetti(enable: Boolean) {
        dispatchAppStoreAction(SettingAction.EnableConfetti(enable))
    }
}