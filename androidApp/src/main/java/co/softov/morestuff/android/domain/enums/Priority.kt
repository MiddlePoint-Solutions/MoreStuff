package co.softov.morestuff.android.domain.enums

sealed interface Priority {
    data class Today(val option: TodayOption = TodayOption.Automatic) : Priority
    data class Tomorrow(val option: TomorrowOption = TomorrowOption.Automatic) : Priority
    data class Later(val option: LaterOption = LaterOption.Automatic) : Priority
}

sealed interface PriorityOption

enum class TodayOption : PriorityOption {
    Automatic, Specify, OneHour, Morning, Noon, Afternoon, Evening, Tonight,
}

enum class TomorrowOption : PriorityOption {
    Automatic, Specify, Morning,  Noon, Afternoon, Evening, Late,
}

enum class LaterOption : PriorityOption {
    Automatic, Custom, Weekend, Someday
}

