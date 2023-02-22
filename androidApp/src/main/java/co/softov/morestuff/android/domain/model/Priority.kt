package co.softov.morestuff.android.domain.model

sealed class Priority(open val option: PriorityOption) {
    data class Today(override val option: PriorityOption = DefaultOption.Auto) : Priority(option)
    data class Tomorrow(override val option: PriorityOption = DefaultOption.Auto) : Priority(option)
    data class Later(override val option: PriorityOption = DefaultOption.Auto) : Priority(option)

    companion object {
        val today = Today()
        val tomorrow = Tomorrow()
        val later = Later()
    }
}



// TODO: Classes implementing this interface should be sealed classes (for better use of kotlin)
sealed interface PriorityOption

enum class DefaultOption : PriorityOption {
    Auto, Custom
}

enum class TimeOfDayOption : PriorityOption {
    Morning, Noon, Afternoon, Evening,
}

enum class LaterOption : PriorityOption {
    Weekend, Someday
}

