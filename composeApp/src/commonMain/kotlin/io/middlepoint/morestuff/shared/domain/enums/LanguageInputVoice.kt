package io.middlepoint.morestuff.shared.domain.enums

enum class Language(val code: String) {
    Device("default"),
    English("en-US"),
    Spanish("es-ES"),
    Hebrew("en-US"),
    Russian("ru-RU"),
    Catalan("ca-Ca");

    companion object {
        operator fun get(index: Int): Language = entries[index]
    }

}