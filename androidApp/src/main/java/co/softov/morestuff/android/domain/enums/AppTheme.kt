package co.softov.morestuff.android.domain.enums

enum class AppTheme {
    MODE_AUTO,
    MODE_DAY,
    MODE_NIGHT;

    companion object {
        fun fromOrdinal(ordinal: Int) = values()[ordinal]
    }
}