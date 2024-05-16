package io.middlepoint.morestuff.android.domain.enums

enum class Language(val code: String) {
    Device("default"),
    English("en"),
    Spanish("es"),
    Hebrew("he"),
    Russian("ru"),
    Catalan("ca");

    companion object {
        operator fun get(index: Int): Language = entries[index]
    }

}