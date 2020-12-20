package co.softov.morestuff.androidApp.domain.enums

import co.softov.morestuff.androidApp.domain.model.LaterOption
import co.softov.morestuff.androidApp.domain.model.TodayOption
import co.softov.morestuff.androidApp.domain.model.TomorrowOption

sealed class Priority {
    data class Today(val option: TodayOption = TodayOption.Automatic) : Priority()
    data class Tomorrow(val option: TomorrowOption = TomorrowOption.Automatic) : Priority()
    data class Later(val option: LaterOption = LaterOption.Automatic) : Priority()
}

