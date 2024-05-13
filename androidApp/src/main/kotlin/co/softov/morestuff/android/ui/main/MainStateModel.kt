package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.model.Shareable

data class MainState(
    val ready: Boolean = false,
    val theme: AppTheme = AppTheme.System,
    val showOnBoarding: Boolean = false,
)

sealed class MainEvent {
    data object OnBoardingComplete : MainEvent()
    data class ShareContent(val taskId: Long, val content: Shareable) : MainEvent()
}
