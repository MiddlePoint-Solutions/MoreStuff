package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.model.SuccessResult
import co.softov.morestuff.android.domain.service.Scheduler

interface ScheduleAtTimeUseCase {
    suspend operator fun invoke(scheduleId: Long, time: String): SimpleResult<Boolean>
}

class ScheduleAtTimeUseCaseImpl(
    private val scheduler: Scheduler
) : ScheduleAtTimeUseCase {

    override suspend fun invoke(scheduleId: Long, time: String): SimpleResult<Boolean> {
        scheduler.scheduleAtExact(scheduleId, time)
        return SuccessResult(true)
    }
}



