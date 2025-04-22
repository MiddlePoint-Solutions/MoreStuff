package io.middlepoint.morestuff.shared.ui.screen.main

import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.Uuid

data class MainState(
    val ready: Boolean = false,
    val theme: AppTheme = AppTheme.System,
    val showOnBoarding: Boolean = false,
)

sealed class MainEvent {
    data object OnBoardingComplete : MainEvent()
    data class ShareContent(val taskId: Uuid, val content: Shareable) : MainEvent()
}
