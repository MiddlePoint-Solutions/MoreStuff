package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.model.Shareable

sealed class MainState {

    data object Loading : MainState()

    data class Ready(
        val theme: AppTheme = AppTheme.System,
        val showOnBoarding: Boolean = false,
    ) : MainState()

}

sealed class MainEvent {
    data object OnBoardingComplete : MainEvent()
    data class ShareContent(val taskId: Long, val content: Shareable) : MainEvent()
}
