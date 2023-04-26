package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetScheduleWithTitleUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure,ScheduleWithTitle>
}

class GetScheduleWithTitleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetScheduleWithTitleUseCase {
    override suspend fun invoke(scheduleId: Long): Either<Failure,ScheduleWithTitle> {
        return scheduleRepository.getActiveScheduleWithTitle(scheduleId)
    }
} 



