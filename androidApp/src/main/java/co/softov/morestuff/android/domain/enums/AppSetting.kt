package co.softov.morestuff.android.domain.enums

sealed class AppSetting<T>(val defaultValue: T) {
    data object FirstTime : AppSetting<Boolean>(true)
    data object Theme : AppSetting<String>(AppTheme.System.name)
    data object SnoozeLimit : AppSetting<Int>(3)
    data object Confetti : AppSetting<Boolean>(true)

}