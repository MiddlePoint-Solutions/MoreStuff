package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.redux.state.AppSettings
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class SettingAction : Action.FeatureAction() {
    data class InitSettings(val settings: AppSettings) : SettingAction()
    data class EnableDevSettings(val enable: Boolean) : SettingAction()
    data class SetSnoozeLimit(val amount: Int) : SettingAction()
    data class SetAppTheme(val theme: AppTheme) : SettingAction()
    data object OnBoardingComplete : SettingAction()
    data class SetReviewTimeAction(val hour: Int, val minute: Int) : SettingAction()
    data class EnableReviewHint(val enable: Boolean) : SettingAction()
    data class SetVoiceLanguage(val language: Language) : SettingAction()
}