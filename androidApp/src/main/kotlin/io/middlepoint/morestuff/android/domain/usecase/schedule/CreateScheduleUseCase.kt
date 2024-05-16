package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScheduleDomain
import io.middlepoint.morestuff.android.domain.enums.ScheduleType
import io.middlepoint.morestuff.android.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.android.domain.service.TimeManager
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

interface CreateScheduleUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleType: ScheduleType,
        localDateTime: LocalDateTime
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
        localDateTime: LocalDateTime
    ): Either<Failure, ScheduleDomain> {
        cancelActiveScheduleUseCase(listOf(taskId), listOf(scheduleType))
        val utcTime = timeManager.localDateTimeToUtc(localDateTime).toString()
        val schedule = ScheduleDomain(
            id = 0,
            taskId = taskId,
            createTime = timeManager.getCreateTime(),
            scheduleLocalTime = localDateTime.toString(),
            scheduleUtcTime = utcTime,
            timezone = timeManager.currentTimeZone.id,
            active = true,
            scheduleType = scheduleType,
        )
        Timber.d("### $scheduleType Scheduling, task $taskId = -> ${schedule.scheduleLocalTime}} ###")
        return scheduleRepository.createSchedule(schedule)
    }
}