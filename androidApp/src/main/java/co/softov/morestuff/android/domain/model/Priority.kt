package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.enums.ReplyType
import kotlinx.datetime.LocalDateTime

sealed class Priority {
    data class Now(val option: PriorityOption = DefaultOption.Auto) : Priority()
    data class Later(val option: PriorityOption = DefaultOption.Auto) : Priority()
    data class Plan(val localTime: LocalDateTime) : Priority()
}

val Priority.replyWithTitle: Pair<String, ReplyType>
    get() = when (this) {
        is Priority.Plan -> "Later" to ReplyType.LATER
        is Priority.Now -> "Snooze" to ReplyType.SNOOZE
        is Priority.Later -> "Tomorrow" to ReplyType.TOMORROW
    }


// TODO: Classes implementing this interface should be sealed classes (for better use of kotlin)
sealed interface PriorityOption

enum class DefaultOption : PriorityOption {
    Auto
}

