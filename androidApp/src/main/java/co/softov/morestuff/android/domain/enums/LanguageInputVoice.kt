package co.softov.morestuff.android.domain.enums

import java.util.Locale

enum class Language(val code: String) {
    DEVICE(Locale.getDefault().language),
    ENGLISH("en"),
    SPANISH("es"),
    HEBREW("he"),
    RUSSIAN("ru"),
    CATALAN("ca");

    companion object {
        operator fun get(index: Int): Language = values()[index]
    }

}


