package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import kotlinx.datetime.LocalDateTime

interface CreateScheduleUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleType: ScheduleType,
        localDateTime: LocalDateTime
    ): Either<Failure, Schedule>
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
    ): Either<Failure, Schedule> {
        cancelActiveScheduleUseCase(listOf(taskId), listOf(scheduleType))
        val utcTime = timeManager.localDateTimeToUtc(localDateTime).toString()
        val schedule = Schedule(
            id = 0,
            taskId = taskId,
            createTime = timeManager.getCreateTime(),
            scheduleLocalTime = localDateTime.toString(),
            scheduleUtcTime = utcTime,
            timezone = timeManager.currentTimeZone.id,
            active = true,
            scheduleType = scheduleType,
        )
        return scheduleRepository.createSchedule(schedule)
    }
}