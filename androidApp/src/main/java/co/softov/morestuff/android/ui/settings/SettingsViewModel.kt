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
    val devTools: DevTools,
) : NoStateViewModel() {

    val model = MutableStateFlow(
        with(store.state.value.settings) {
            SettingsModel(
                appTheme = appTheme,
                snoozeLimit = snoozeLimit,
            )
        }
    )

    init {
        loadData()
    }

    override fun onAppStateChange(state: AppState) {
        model.update {
            with(state.settings) {
                it.copy(
                    appTheme = appTheme,
                    snoozeLimit = snoozeLimit,
                    confettiEnabled = enableConfetti
                )
            }
        }
    }

    fun onSnoozeLimitChanged(limit: Int) {
        dispatchAppStoreAction(SettingAction.SetSnoozeLimit(limit))
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