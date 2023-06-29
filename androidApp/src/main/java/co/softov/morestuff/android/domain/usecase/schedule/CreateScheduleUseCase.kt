package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import timber.log.Timber

interface CreateScheduleUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleType: ScheduleType,
        localDateTime: String
    ): Either<Failure, ScheduleDomain>
}

class CreateScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
) : CreateScheduleUseCase {
    override suspend fun invoke(
        taskId: Long,
        scheduleType: ScheduleType,
        localDateTime: String
    ): Either<Failure, ScheduleDomain> {
        cancelActiveScheduleUseCase(taskId)
        val utcTime = timeManager.localDateTimeStringToUtc(localDateTime).toString()
        val schedule = ScheduleDomain(
            id = 0,
            taskId = taskId,
            createTime = timeManager.getCreateTime(),
            scheduleLocalTime = localDateTime,
            scheduleUtcTime = utcTime,
            timezone = timeManager.currentTimeZone.id,
            active = true,
            scheduleType = scheduleType,
        )
        Timber.d("### $scheduleType Scheduling, task $taskId = -> ${schedule.scheduleLocalTime}} ###")
        return scheduleRepository.createSchedule(schedule)
    }
}