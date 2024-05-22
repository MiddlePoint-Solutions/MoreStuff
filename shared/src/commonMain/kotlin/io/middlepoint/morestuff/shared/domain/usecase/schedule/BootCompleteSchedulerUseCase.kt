package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.data.utils.scheduleLocalDateTime
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.middlepoint.morestuff.shared.domain.service.TimeManager

interface BootCompleteSchedulerUseCase {
    suspend operator fun invoke(): Either<Failure, Boolean>
}

class BootCompleteSchedulerUseCaseImpl(
    private val scheduleWorkUseCase: ScheduleWorkUseCase,
    private val getAllActiveSchedulesUseCase: GetAllActiveSchedulesUseCase,
    private val scheduler: Scheduler,
    private val timeManager: TimeManager,
) : BootCompleteSchedulerUseCase {

    override suspend fun invoke(): Either<Failure, Boolean> {
//        scheduleWorkUseCase()
        return when (val result = getAllActiveSchedulesUseCase()) {
            is Either.Left -> result
            is Either.Right -> {
//                reschedule(result.value.toMutableList()) // Joseph: do we really need to reschedule here? unless they are exact schedule.
                Either.Right(true)
            }
        }
    }

    private fun reschedule(activeSchedules: MutableList<ScheduleDomain>) {
        val currentTime = timeManager.nowLocalDateTime

        val futureSchedules = activeSchedules.filter { schedule ->
            schedule.scheduleLocalDateTime?.let { it > currentTime } ?: false
        }

        futureSchedules.forEach { schedule ->
            schedule.scheduleLocalTime?.let { time ->
                scheduler.scheduleAtExact(schedule.id, time)
            }
        }
    }
}