package co.softov.morestuff.android.ui.model.map

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.ui.model.ScheduleListItemViewModel

class ScheduleListItemMapper {
    fun map(input: ScheduleWithTitle): ScheduleListItemViewModel {

        val scheduleTime = when (input.scheduleTime) {
            null -> "Later"
            else -> input.scheduleTime
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