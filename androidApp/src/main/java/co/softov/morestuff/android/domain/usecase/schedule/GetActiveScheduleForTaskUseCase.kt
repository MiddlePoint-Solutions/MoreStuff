package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveScheduleForTaskUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, ScheduleDomain>
}

class GetActiveScheduleForTaskUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveScheduleForTaskUseCase {

    override suspend fun invoke(taskId: Long): Either<Failure, ScheduleDomain> {
        return scheduleRepository.getActiveScheduleForTask(taskId)
    }
}

