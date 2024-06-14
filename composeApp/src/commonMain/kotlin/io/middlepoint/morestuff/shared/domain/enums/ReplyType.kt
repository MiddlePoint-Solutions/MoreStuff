package io.middlepoint.morestuff.shared.domain.enums

enum class ReplyType(val value: Int) {
    LATER(0),
    SNOOZE(100),
    TOMORROW(200),
    DONE(300);

    companion object {
        fun withValue(value: Int) = run { values().first { it.value == value } }
    }
}

val ReplyType.displayTitle: String
    get() = when (this) {
        ReplyType.LATER -> "Later"
        ReplyType.SNOOZE -> "Snooze"
        ReplyType.TOMORROW -> "Tomorrow"
        ReplyType.DONE -> "Done"
    }
