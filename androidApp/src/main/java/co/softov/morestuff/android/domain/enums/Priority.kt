package co.softov.morestuff.android.domain.enums

sealed interface Priority {
    data class Today(val option: TodayOption = TodayOption.Auto) : Priority
    data class Tomorrow(val option: TomorrowOption = TomorrowOption.Auto) : Priority
    data class Later(val option: LaterOption = LaterOption.Auto) : Priority
}

sealed interface PriorityOption

enum class TodayOption : PriorityOption {
    Auto, Specify, OneHour, Morning, Noon, Afternoon, Evening, Tonight,
}

enum class TomorrowOption : PriorityOption {
    Auto, Specify, Morning,  Noon, Afternoon, Evening, Late,
}

enum class LaterOption : PriorityOption {
    Auto, Custom, Weekend, Someday
}

