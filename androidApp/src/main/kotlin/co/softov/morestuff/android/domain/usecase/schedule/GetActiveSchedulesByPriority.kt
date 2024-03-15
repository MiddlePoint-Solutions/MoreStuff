package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain

interface GetActiveSchedulesByPriority {
    suspend operator fun invoke(
        priority: Priority
    ): Either<Failure, List<ScheduleDomain>>
}

class GetActiveSchedulesByPriorityImpl(
    private val getAllActiveSchedulesUseCase: GetAllActiveSchedulesUseCase
) : GetActiveSchedulesByPriority {

    override suspend fun invoke(
        priority: Priority
    ): Either<Failure, List<ScheduleDomain>> = when (priority) {
        is Priority.Now -> {
            if (priority.option == DefaultOption.Auto) {
                // TODO(Alex) write tests for use cases (SQL and everything)
                // TODO(Joseph) fetch schedules that are active in the past hour
                // This should also take into account previous active schedules that have been left behind.
            }
            getAllActiveSchedulesUseCase()
        }
        is Priority.Later -> TODO("Implement get tomorrow priority")
        is Priority.Plan -> TODO("Implement get Later priority")
    }
}