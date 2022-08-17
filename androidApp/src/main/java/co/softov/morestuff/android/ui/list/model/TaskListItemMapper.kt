package co.softov.morestuff.android.ui.list.model

import co.softov.morestuff.android.domain.model.Scope
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat

class TaskListItemMapper {

    private val dateFormat =
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    fun map(input: Scope): TaskListItemViewModel {
        val createTime = input.createTime.toLocalDateTime().toString()
        val completeTime = when (input.completeTime) {
            null -> ""
            else -> input.completeTime.toLocalDateTime().toString()
        }

        return TaskListItemViewModel(
            id = input.id,
            createTime = createTime,
            completeTime = completeTime,
            title = input.title
        )
    }

    fun map(input: List<Scope>): List<TaskListItemViewModel> {
        return input.map { map(it) }
    }
}