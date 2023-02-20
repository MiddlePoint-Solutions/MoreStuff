package co.softov.morestuff.android.ui.list.model

import co.softov.morestuff.android.domain.model.Task
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TaskListItemMapper {

    private val dateFormat =
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    fun map(input: Task): TaskListItemViewModel {
        val inputDateTime = ZonedDateTime.now().toString()
        val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
        val createTime = ZonedDateTime.parse(inputDateTime, formatter)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))



        //val createTime = input.createTime.toLocalDateTime().toString()
        val completeTime = when (input.completeTime) {
            null -> ""
            else -> ""//input.completeTime.toLocalDateTime().toString()
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