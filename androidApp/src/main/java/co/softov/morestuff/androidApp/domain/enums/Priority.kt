package co.softov.morestuff.androidApp.domain.enums

import co.softov.morestuff.androidApp.domain.enums.TimeOption.Default

sealed class Priority {
    data class Today(val option: TimeOption = Default) : Priority()
    data class Tomorrow(val option: TimeOption = Default) : Priority()
    data class Later(val option: TimeOption = Default) : Priority()
}

sealed class TimeOption {
    object Default : TimeOption()
}

