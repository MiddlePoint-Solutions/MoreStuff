package io.middlepoint.morestuff.android.data

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.DevSettings
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.FirstTime
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.ReviewTime
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.SnoozeLimit
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.Theme

val AppSetting<*>.key: String
    get() = when (this) {
        FirstTime -> Constants.KEY_FIRST_TIME
        Theme -> Constants.KEY_APP_THEME
        SnoozeLimit -> Constants.KEY_USER_SNOOZE_LIMIT
        DevSettings -> Constants.KEY_DEV_SETTINGS
        ReviewTime -> Constants.KEY_REVIEW_TIME
        AppSetting.ShowHintArrowPriority -> Constants.KEY_REVIEW_HINT
        AppSetting.VoiceInputLanguage -> Constants.KEY_LANGUAGE_INPUT
    }