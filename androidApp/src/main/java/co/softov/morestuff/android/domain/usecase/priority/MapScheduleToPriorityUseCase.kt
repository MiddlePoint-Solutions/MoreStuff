package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import co.softov.morestuff.android.domain.model.*
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.usecase.BaseUseCase
import kotlinx.datetime.toLocalDateTime

interface MapScheduleToPriorityUseCase :
    BaseUseCase<Failure, Schedule, Priority> {

}

class MapScheduleToPriorityUseCaseImpl( val timeManager: TimeManager) :
    MapScheduleToPriorityUseCase {

    override suspend fun invoke(params: Schedule): Either<Failure, Priority> {
        val priority = params.scheduleLocalTime?.let {
            val scheduleTime = it.toLocalDateTime()
            when {
                timeManager.isToday(it) -> {
                    val morning = timeManager.todayLocalDateTime(8)
                    val noon = timeManager.todayLocalDateTime(12)
                    val afternoon = timeManager.todayLocalDateTime(18)
                    val night = timeManager.todayLocalDateTime(18)

                    val option = when {
                        scheduleTime >= morning && scheduleTime < noon -> TimeOfDayOption.Morning
                        scheduleTime >= noon && scheduleTime < afternoon -> TimeOfDayOption.Afternoon
                        scheduleTime >= afternoon && scheduleTime < night -> TimeOfDayOption.Evening
                        else -> DefaultOption.Auto
                    }

                    Priority.Now(option = option)
                }
                timeManager.isTomorrow(it) -> {
                    val morning = timeManager.tomorrowLocalDateTime(8)
                    val noon = timeManager.tomorrowLocalDateTime(12)
                    val afternoon = timeManager.tomorrowLocalDateTime(18)
                    val night = timeManager.tomorrowLocalDateTime(21)

                    val option = when {
                        scheduleTime >= morning && scheduleTime < noon -> TimeOfDayOption.Morning
                        scheduleTime >= noon && scheduleTime < afternoon -> TimeOfDayOption.Afternoon
                        scheduleTime >= afternoon && scheduleTime < night -> TimeOfDayOption.Evening
                        else -> DefaultOption.Auto
                    }

                    Priority.Later(option = option)
                }
                timeManager.isLater(it) -> {
                    Priority.Plan("")
                }
                else -> Priority.Now()
            }
        } ?: Priority.Later()
        return Either.Right(priority)
    }
}