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
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
        val inputDateTime = ZonedDateTime.now().toString()

        val createTime: String
        val completeTime: String? = if (input.completeTime != null) {
            val completeTimeFormatted = markCompleteTime(input.completeTime)
            val zonedDateTime = ZonedDateTime.parse(completeTimeFormatted, formatter)
            createTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(formatter)
            completeTimeFormatted
        } else {
            val zonedDateTime = ZonedDateTime.parse(inputDateTime, DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()))
            createTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(formatter)
            null
        }




    return TaskListItemViewModel(
            id = input.id,
            createTime = createTime,
            completeTime = completeTime?:"",
            title = input.title
        )
    }

    private fun markCompleteTime(timeString: String?): String? {
        return timeString?.let {
            val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
            val localDateTime = ZonedDateTime.parse(it, formatter)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
            val timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            localDateTime.format(timeFormatter)
        }
    }
    fun map(input: List<Task>): List<TaskListItemViewModel> {
        return input.map { map(it) }
    }
}