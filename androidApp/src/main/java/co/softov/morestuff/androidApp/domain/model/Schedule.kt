package co.softov.morestuff.androidApp.domain.model

import co.softov.morestuff.androidApp.domain.enums.Message

data class Schedule(
    val id: Long,
    val taskId: Long,
    val createTime: Long,
    val scheduleTime: Long,
    val type: Message,
    val fulfilled: Boolean
) {
    companion object {
        const val LATER_TASK = 0L
    }
}
