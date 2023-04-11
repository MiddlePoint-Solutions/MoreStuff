package co.softov.morestuff.android.ui.list.model

import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TaskListItemMapper(private val timeFormatter: TimeFormatter) {

    fun map(input: Task): TaskListItemViewModel {
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
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
            val zonedDateTime = ZonedDateTime.parse(
                inputDateTime,
                DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
            )
            createTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(formatter)
            null
        }


        return TaskListItemViewModel(
            id = input.id,
            createTime = createTime,
            completeTime = completeTime ?: "",
            title = input.title
        )
    }

    private fun markCompleteTime(timeString: String?): String? {
        return timeFormatter.formatTime(timeString, "dd/MM/yyyy HH:mm")
    }

    fun map(input: List<Task>): List<TaskListItemViewModel> {
        return input.map { map(it) }
    }
}
