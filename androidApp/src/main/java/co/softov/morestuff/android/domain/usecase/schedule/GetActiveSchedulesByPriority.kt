package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule

interface GetActiveSchedulesByPriority {
    suspend operator fun invoke(
        priority: Priority
    ): Either<Failure, List<Schedule>>
}

class GetActiveSchedulesByPriorityImpl(
    private val getActiveSchedulesUseCase: GetActiveSchedulesUseCase
) : GetActiveSchedulesByPriority {

    override suspend fun invoke(
        priority: Priority
    ): Either<Failure, List<Schedule>> = when (priority) {
        is Priority.Today -> {
            if (priority.option == DefaultOption.Auto) {
                // TODO(Alex) write tests for use cases (SQL and everything)
                // TODO(Joseph) fetch schedules that are active in the past hour
                // This should also take into account previous active schedules that have been left behind.
            }
            getActiveSchedulesUseCase()
        }
        is Priority.Tomorrow -> TODO("Implement get tomorrow priority")
        is Priority.Later -> TODO("Implement get Later priority")
    }
}