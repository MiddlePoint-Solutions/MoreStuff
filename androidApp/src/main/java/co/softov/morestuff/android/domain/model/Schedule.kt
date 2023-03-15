package co.softov.morestuff.android.domain.model

data class Schedule(
    val id: Long,
    val taskId: Long,
    val createTime: String,
    val scheduleLocalTime: String?,
    val scheduleUtcTime: String?,
    val timezone: String,
    val active: Boolean
) {
    companion object {
        const val LATER_TASK = 0L

        fun empty() = Schedule(
            id = 0,
            taskId = 1,
            createTime = "",
            scheduleLocalTime = null,
            scheduleUtcTime = null,
            timezone = "",
            active = false,
        )
    }
}
