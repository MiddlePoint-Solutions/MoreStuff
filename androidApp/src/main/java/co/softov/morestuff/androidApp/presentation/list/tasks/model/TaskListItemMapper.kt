package co.softov.morestuff.androidApp.presentation.list.tasks.model

import co.softov.morestuff.androidApp.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Date

class TaskListItemMapper {

    private val dateFormat =
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    fun map(input: Task): TaskListItemViewModel {
        val createTime = dateFormat.format(Date(input.createTime))
        val completeTime = when (input.completeTime) {
            0L -> ""
            else -> dateFormat.format(Date(input.completeTime))
        }

        return TaskListItemViewModel(
            id = input.id,
            createTime = createTime,
            completeTime = completeTime,
            title = input.title
        )
    }

    fun map(input: List<Task>): List<TaskListItemViewModel> {
        return input.map { map(it) }
    }
}