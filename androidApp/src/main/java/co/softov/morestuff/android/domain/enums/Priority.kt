package co.softov.morestuff.android.domain.enums

sealed interface Priority {
    data class Today(val option: PriorityOption = DefaultOption.Auto) : Priority
    data class Tomorrow(val option: PriorityOption = DefaultOption.Auto) : Priority
    data class Later(val option: PriorityOption = DefaultOption.Auto) : Priority
}

sealed interface PriorityOption

enum class DefaultOption : PriorityOption {
    Auto, Specify
}

enum class TimeOfDayOption : PriorityOption {
    Morning, Noon, Afternoon, Evening,
}

enum class LaterOption : PriorityOption {
    Weekend, Someday
}

