package co.softov.morestuff.android.domain.enums

enum class ContentType(val value: Int) {
    USER_NEW_TASK(100),
    CONFIRM_NEW_TASK(101),
    TASK_REMINDER(200),
    TASK_MESSAGE(201),
    APP_TASK_MESSAGE(202);

    companion object {
        fun withValue(value: Int) = run { values().first { it.value == value } }
    }
}