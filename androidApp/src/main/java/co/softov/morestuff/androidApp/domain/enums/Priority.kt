package co.softov.morestuff.androidApp.domain.enums

import co.softov.morestuff.androidApp.domain.model.LaterOption
import co.softov.morestuff.androidApp.domain.model.PriorityOption
import co.softov.morestuff.androidApp.domain.model.TodayOption
import co.softov.morestuff.androidApp.domain.model.TomorrowOption

sealed class Priority(val value: PriorityOption) {
    data class Today(val option: TodayOption = TodayOption.Automatic) : Priority(option)
    data class Tomorrow(val option: TomorrowOption = TomorrowOption.Automatic) : Priority(option)
    data class Later(val option: LaterOption = LaterOption.Automatic) : Priority(option)
}

