package co.softov.morestuff.android.data

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppSetting.Confetti
import co.softov.morestuff.android.domain.enums.AppSetting.DevSettings
import co.softov.morestuff.android.domain.enums.AppSetting.FirstTime
import co.softov.morestuff.android.domain.enums.AppSetting.ReviewTime
import co.softov.morestuff.android.domain.enums.AppSetting.SnoozeLimit
import co.softov.morestuff.android.domain.enums.AppSetting.Theme

val AppSetting<*>.key: String
    get() = when (this) {
        FirstTime -> Constants.KEY_FIRST_TIME
        Theme -> Constants.KEY_APP_THEME
        SnoozeLimit -> Constants.KEY_USER_SNOOZE_LIMIT
        Confetti -> Constants.KEY_ENABLE_CONFETTI
        DevSettings -> Constants.KEY_DEV_SETTINGS
        ReviewTime -> Constants.KEY_REVIEW_TIME
        AppSetting.ShowHintArrowPriority -> Constants.KEY_REVIEW_HINT
        AppSetting.VoiceInputLanguage -> Constants.KEY_LANGUAGE_INPUT
    }