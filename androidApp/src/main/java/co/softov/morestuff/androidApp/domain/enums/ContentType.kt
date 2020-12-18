package co.softov.morestuff.androidApp.domain.enums

enum class ContentType(val value: Int) {
    INVALID(0),
    USER_NEW_TASK(100),
    CONFIRM_NEW_TASK(101),
    TASK_REMINDER(200);

    companion object {
        fun withValue(value: Int) = run { values().first { it.value == value } }
    }
}