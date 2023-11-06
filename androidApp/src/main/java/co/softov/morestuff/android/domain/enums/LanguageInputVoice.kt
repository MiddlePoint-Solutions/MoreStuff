package co.softov.morestuff.android.domain.enums

enum class Language(val code: String) {
    DEVICE("device language"),
    ENGLISH("en"),
    SPANISH("es"),
    HEBREW("he"),
    RUSSIAN("ru");

    companion object {
        operator fun get(index: Int): Language = values()[index]
    }
}