package co.softov.morestuff.android.domain.enums

enum class AppTheme {
    System,
    Light,
    Dark;

    companion object {
        operator fun get(index: Int): AppTheme = values()[index]
    }
}

