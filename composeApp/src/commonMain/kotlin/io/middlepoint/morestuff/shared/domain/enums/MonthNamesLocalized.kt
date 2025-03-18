package io.middlepoint.morestuff.shared.domain.enums

import kotlinx.datetime.format.MonthNames

enum class MonthNamesLocalized(val monthNames: MonthNames) {
  ENGLISH(MonthNames(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  )),

  SPANISH(MonthNames(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
  ));

  companion object {
    fun fromLanguageCode(code: String): MonthNames {
      return when (code) {
        "es" -> SPANISH.monthNames
        else -> ENGLISH.monthNames
      }
    }
  }
}
