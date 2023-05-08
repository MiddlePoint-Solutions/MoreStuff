package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetActiveScheduleFlowUseCase {
    operator fun invoke(taskId: Long): Flow<Either<Failure, ScheduleDomain>>
}

class GetActiveScheduleFlowUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveScheduleFlowUseCase {
    override fun invoke(taskId: Long): Flow<Either<Failure, ScheduleDomain>> {
        return scheduleRepository.getActiveScheduleForTaskFlow(taskId)
    }
}