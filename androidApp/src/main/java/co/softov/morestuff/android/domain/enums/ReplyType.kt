package co.softov.morestuff.android.domain.enums

enum class ReplyType(val value: Int) {
    LATER(0),
    SNOOZE(100),
    TOMORROW(200),
    DONE(300);

    companion object {
        fun withValue(value: Int) = run { values().first { it.value == value } }
    }
}