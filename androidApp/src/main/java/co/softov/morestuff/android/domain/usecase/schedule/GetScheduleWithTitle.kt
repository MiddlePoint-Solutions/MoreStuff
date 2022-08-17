package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetScheduleWithTitle {
    suspend operator fun invoke(scheduleId: Long): Either<Failure,ScheduleWithTitle>
}

class GetScheduleWithTitleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetScheduleWithTitle {
    override suspend fun invoke(scheduleId: Long): Either<Failure,ScheduleWithTitle> {
        return scheduleRepository.getActiveScheduleWithTitle(scheduleId)
    }
} 



