package co.softov.morestuff.android.domain.model

data class Schedule(
    val id: Long,
    val taskId: Long,
    val createTime: String,
    val scheduleTimeLocal: String?,
    val scheduleTimeUtc: String?,
    val timezone: String,
    val active: Boolean
) {
    companion object {
        const val LATER_TASK = 0L

        fun empty() = Schedule(
            id = 0,
            taskId = 1,
            createTime = "",
            scheduleTimeLocal = null,
            scheduleTimeUtc = null,
            timezone = "",
            active = false,
        )
    }
}
