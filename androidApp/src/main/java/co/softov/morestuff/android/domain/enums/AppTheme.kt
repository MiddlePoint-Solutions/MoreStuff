package co.softov.morestuff.android.domain.enums

enum class AppTheme {
    System,
    Light,
    Dark;

    companion object {
        fun fromOrdinal(ordinal: Int) = values()[ordinal]

        fun listValues() = values().toList()
        operator fun get(index: Int): AppTheme = values()[index]
    }
}

