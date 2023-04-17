package co.softov.morestuff.android.ui.list.model

import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter

class TaskListItemMapper(private val timeFormatter: TimeFormatter, private val timeManager: TimeManager) {
    fun map(input: Task): TaskListItemViewModel {
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

    fun map(input: List<Task>): List<TaskListItemViewModel> {
        return input.map { map(it) }
    }
}