package co.softov.morestuff.android.ui.settings

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.enums.Language

@Immutable
data class SettingsState(
    val appTheme: AppTheme = AppTheme.System,
    val snoozeLimit: Int = 0,
    val devSettings: Boolean = false,
    val reviewTime: Pair<Int, Int> = Pair(9,0),
    val inputVoiceLanguage: Language = Language.Device
) : BaseViewState

@Immutable
data class SettingsActions(
    val selectAppTheme: (Int) -> Unit = {},
    val setSnoozeLimit: (Int) -> Unit = {},
    val enableDevSettings: () -> Unit = {},
    val onTimeSelected: (hour: Int, minute: Int) -> Unit = { _, _ -> },
    val inputVoiceLanguage: (Int) -> Unit = {},
)

sealed class SettingsEvent {
    data class ChangeSnoozeLimit(val limit: Int) : SettingsEvent()
    data class SelectAppTheme(val index: Int) : SettingsEvent()
    data object EnableDevSettings : SettingsEvent()
    data class SetReviewTime(val hour: Int, val minute: Int) : SettingsEvent()
    data class SelectLanguage(val index: Int) : SettingsEvent()
}