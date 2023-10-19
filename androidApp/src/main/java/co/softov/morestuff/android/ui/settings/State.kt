package co.softov.morestuff.android.ui.settings

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.enums.AppTheme

@Immutable
data class SettingsModel(
    val appTheme: AppTheme = AppTheme.System,
    val snoozeLimit: Int = 0,
    val confettiEnabled: Boolean = true,
    val devSettings: Boolean = false,
    val reviewTime: Pair<Int, Int> = Pair(9,0)

) : BaseViewState

@Immutable
data class SettingsActions(
    val selectAppTheme: (Int) -> Unit = {},
    val setSnoozeLimit: (Int) -> Unit = {},
    val enableConfetti: (Boolean) -> Unit = {},
    val enableDevSettings: () -> Unit = {},
    val onTimeSelected: (hour: Int, minute: Int) -> Unit = { _, _ -> }
)