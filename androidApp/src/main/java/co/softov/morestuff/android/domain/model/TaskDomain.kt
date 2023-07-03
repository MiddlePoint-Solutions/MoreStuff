package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.enums.TaskType

data class TaskDomain(
    val id: Long = 0L,
    val uuid: String = "",
    val title: String = "",
    val createTime: String = "",
    val completeTime: String? = null,
    val priorityScore: Long = 0,
    val taskType: TaskType = TaskType.System,
    val activeSchedule: ScheduleDomain? = null
) {

    val isComplete: Boolean get() = completeTime != null
    val hasSchedule: Boolean get() = activeSchedule?.isOneTime() ?: false
    val hasReminder: Boolean get() = activeSchedule?.isReminder() ?: false

}