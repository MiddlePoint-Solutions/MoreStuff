package io.middlepoint.morestuff.shared.domain.enums

sealed class AppSetting<T>(val defaultValue: T) {
    data object DevSettings : AppSetting<Boolean>(false)
    data object FirstTime : AppSetting<Boolean>(true)
    data object Theme : AppSetting<String>(AppTheme.System.name)
    data object SnoozeLimit : AppSetting<Int>(3)
    data object ReviewTime : AppSetting<Pair<Int, Int>>(9 to 0)
    data object ShowHintArrowPriority : AppSetting<Boolean>(true)
    data object VoiceInputLanguage : AppSetting<String>(Language.Device.name)
}