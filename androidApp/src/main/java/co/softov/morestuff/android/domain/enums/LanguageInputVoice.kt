package co.softov.morestuff.android.domain.enums

enum class Language(val code: String) {
    DEVICE("default"),
    ENGLISH("en"),
    SPANISH("es"),
    HEBREW("he"),
    RUSSIAN("ru"),
    CATALAN("ca");

    companion object {
        operator fun get(index: Int): Language = values()[index]
    }

}


