package io.middlepoint.morestuff.shared.ui.screen.main

import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.model.Shareable

data class MainState(
    val ready: Boolean = false,
    val theme: AppTheme = AppTheme.System,
    val showOnBoarding: Boolean = false,
)

sealed class MainEvent {
    data object OnBoardingComplete : MainEvent()
    data class ShareContent(val taskId: Long, val content: Shareable) : MainEvent()
}
