package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetActiveScheduleFlowUseCase {
    operator fun invoke(taskId: Long): Flow<Either<Failure, Schedule>>
}

class GetActiveScheduleFlowUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveScheduleFlowUseCase {
    override fun invoke(taskId: Long): Flow<Either<Failure, Schedule>> {
        return scheduleRepository.getActiveScheduleForTaskFlow(taskId)
    }
}