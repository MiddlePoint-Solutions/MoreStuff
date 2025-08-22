package io.middlepoint.morestuff.shared.domain.enums

enum class ContentType(val value: Int) {
    USER_TASK(100),
    CONFIRM_TASK(101),
    TASK_REMINDER(200),
    TASK_MESSAGE(201),
    APP_TASK_MESSAGE(202),
    AI_TASK_MESSAGE(203);

    companion object {
        fun withValue(value: Int) = run { entries.first { it.value == value } }
    }
}