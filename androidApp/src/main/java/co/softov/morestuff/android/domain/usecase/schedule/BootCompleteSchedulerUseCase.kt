package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.data.utils.scheduleLocalDateTime
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.service.TimeManager
import timber.log.Timber

interface BootCompleteSchedulerUseCase {
    suspend operator fun invoke(): Either<Failure, Boolean>
}

class BootCompleteSchedulerUseCaseImpl(
    private val scheduleNotificationsAndWorkUseCase: ScheduleNotificationsAndWorkUseCase,
    private val getActiveSchedulesUseCase: GetActiveSchedulesUseCase,
    private val scheduler: Scheduler,
    private val timeManager: TimeManager,
) : BootCompleteSchedulerUseCase {

    override suspend fun invoke(): Either<Failure, Boolean> {
        scheduleNotificationsAndWorkUseCase()
        return when (val result = getActiveSchedulesUseCase()) {
            is Either.Left -> result
            is Either.Right -> {
//                reschedule(result.value.toMutableList()) // Joseph: do we really need to reschedule here? unless they are exact schedule.
                Either.Right(true)
            }
        }
    }

    private fun reschedule(activeSchedules: MutableList<ScheduleDomain>) {
        val currentTime = timeManager.nowLocalDateTime
        Timber.d("BootComplete, Current time: $currentTime")
        Timber.d("BootComplete, active schedules: ${activeSchedules.size}")

        val futureSchedules = activeSchedules.filter { schedule ->
            schedule.scheduleLocalDateTime?.let { it > currentTime } ?: false
        }

        Timber.d("BootComplete, rescheduling future tasks: ${futureSchedules.size}")
        futureSchedules.forEach { schedule ->
            schedule.scheduleLocalTime?.let { time ->
                scheduler.scheduleAtExact(schedule.id, time)
            }
        }
    }
}