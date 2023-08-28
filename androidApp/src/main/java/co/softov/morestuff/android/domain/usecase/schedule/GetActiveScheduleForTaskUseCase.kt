package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveScheduleForTaskUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType> = ScheduleType.values().toList()
    ): Either<Failure, List<ScheduleDomain>>
}

class GetActiveScheduleForTaskUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveScheduleForTaskUseCase {

    override suspend fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> {
        return scheduleRepository.getActiveSchedulesForTask(taskId, scheduleType)
    }
}

