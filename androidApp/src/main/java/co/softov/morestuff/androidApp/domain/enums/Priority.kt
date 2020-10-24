package co.softov.morestuff.androidApp.domain.enums

sealed class Priority {
    data class Today(val option: TimeOption) : Priority()
    data class Tomorrow(val option: TimeOption) : Priority()
    data class Later(val option: TimeOption) : Priority()
}

sealed class TimeOption {
    object Default : TimeOption()
}

