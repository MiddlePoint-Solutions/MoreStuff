package io.middlepoint.morestuff.android.ui.main

import io.middlepoint.morestuff.android.domain.enums.AppTheme
import io.middlepoint.morestuff.android.domain.model.Shareable

data class MainState(
    val ready: Boolean = false,
    val theme: AppTheme = AppTheme.System,
    val showOnBoarding: Boolean = false,
)

sealed class MainEvent {
    data object OnBoardingComplete : MainEvent()
    data class ShareContent(val taskId: Long, val content: Shareable) : MainEvent()
}
