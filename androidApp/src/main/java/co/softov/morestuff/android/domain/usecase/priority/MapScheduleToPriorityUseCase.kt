package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.*
import co.softov.morestuff.android.domain.usecase.BaseUseCase
import kotlinx.datetime.toLocalDateTime

interface MapScheduleToPriorityUseCase :
    BaseUseCase<Failure, Schedule, Priority>

class MapScheduleToPriorityUseCaseImpl : MapScheduleToPriorityUseCase {

    override suspend fun invoke(params: Schedule): Either<Failure, Priority> {
        val priority = params.scheduleLocalTime?.let {
            val scheduleTime = it.toLocalDateTime()
            when {
                TimeUtils.isToday(it) -> {
                    val morning = TimeUtils.todayLocalDateTime(8)
                    val noon = TimeUtils.todayLocalDateTime(12)
                    val afternoon = TimeUtils.todayLocalDateTime(18)
                    val night = TimeUtils.todayLocalDateTime(18)

                    val option = when {
                        scheduleTime >= morning && scheduleTime < noon -> TimeOfDayOption.Morning
                        scheduleTime >= noon && scheduleTime < afternoon -> TimeOfDayOption.Afternoon
                        scheduleTime >= afternoon && scheduleTime < night -> TimeOfDayOption.Evening
                        else -> DefaultOption.Auto
                    }

                    Priority.today.copy(option = option)
                }
                TimeUtils.isTomorrow(it) -> {
                    val morning = TimeUtils.tomorrowLocalDateTime(8)
                    val noon = TimeUtils.tomorrowLocalDateTime(12)
                    val afternoon = TimeUtils.tomorrowLocalDateTime(18)
                    val night = TimeUtils.tomorrowLocalDateTime(21)

                    val option = when {
                        scheduleTime >= morning && scheduleTime < noon -> TimeOfDayOption.Morning
                        scheduleTime >= noon && scheduleTime < afternoon -> TimeOfDayOption.Afternoon
                        scheduleTime >= afternoon && scheduleTime < night -> TimeOfDayOption.Evening
                        else -> DefaultOption.Auto
                    }

                    Priority.tomorrow.copy(option = option)
                }
                TimeUtils.isLater(it) -> {
                    Priority.later
                }
                else -> Priority.today
            }
        } ?: Priority.later
        return Either.Right(priority)
    }
}