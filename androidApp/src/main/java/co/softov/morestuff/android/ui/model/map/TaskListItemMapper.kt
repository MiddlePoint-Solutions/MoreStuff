package co.softov.morestuff.android.ui.model.map

import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.model.TaskListItemViewModel

class TaskListItemMapper(
    private val timeFormatter: TimeFormatter,
    private val timeManager: TimeManager,
) {
    fun map(input: TaskDomain): TaskListItemViewModel {
        val inputDateTime = timeManager.nowLocalDateTimeString

        val createTime: String
        val completeTime: String? = if (input.completeTime != null) {
            createTime = timeFormatter.formatToDateTime(input.completeTime) ?: ""
            timeFormatter.formatToDateTime(input.completeTime)
        } else {
            createTime = timeFormatter.formatToDateTime(inputDateTime) ?: ""
            null
        }

        return TaskListItemViewModel(
            id = input.id,
            createTime = createTime,
            completeTime = completeTime ?: "",
            title = input.title
        )
    }

    fun map(input: List<TaskDomain>): List<TaskListItemViewModel> {
        return input.map { map(it) }
    }
}