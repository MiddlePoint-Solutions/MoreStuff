package co.softov.morestuff.androidApp.presentation.list.schedule.model

import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import java.text.SimpleDateFormat
import java.util.Date

class ScheduleListItemMapper {

    fun map(input: ScheduleWithTitle): ScheduleListItemViewModel {

        val dateFormat =
            SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

        val scheduleTime = when (input.scheduleTime) {
            0L -> "Later"
            else -> dateFormat.format(Date(input.scheduleTime))
        }

        return ScheduleListItemViewModel(
            scheduleId = input.scheduleId,
            taskId = input.taskId,
            scheduleTime = scheduleTime,
            taskTitle = input.taskTitle
        )
    }

    fun map(input: List<ScheduleWithTitle>): List<ScheduleListItemViewModel> {
        return input.map { map(it) }
    }
}