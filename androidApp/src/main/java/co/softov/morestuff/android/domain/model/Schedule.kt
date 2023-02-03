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
    }
}
