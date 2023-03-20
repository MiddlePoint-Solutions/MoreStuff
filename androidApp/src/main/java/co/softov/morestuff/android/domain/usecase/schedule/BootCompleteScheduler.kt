package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.data.utils.scheduleLocalDateTime
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.model.Schedule
import timber.log.Timber

interface BootCompleteScheduler {
    suspend operator fun invoke(): Either<Failure,Boolean>
}

class BootCompleteSchedulerImpl(
    private val getActiveSchedules: GetActiveSchedules,
    private val scheduler: Scheduler
) : BootCompleteScheduler {

    override suspend fun invoke(): Either<Failure,Boolean> {
        return when (val result = getActiveSchedules()) {
            is Either.Left -> result
            is Either.Right -> {
                reschedule(result.value.toMutableList())
                Either.Right(true)
            }
        }
    }

    private fun reschedule(activeSchedules: MutableList<Schedule>) {
        val currentTime = TimeUtils.nowLocalDateTime
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

        // TODO: Get missed schedules that were not fulfilled and create reminders for them
        // TODO: should update the current schedule or create a new one?
        // TODO: How are we going to display this?
        //  What if there is a schedule that should show now and missed schedules?
        //  The priority goes to the current scheduled reminder and the missed schedules should be shown at a different time.
        activeSchedules.removeAll(futureSchedules)
        Timber.d("execute, missed schedules: ${activeSchedules.size}")
        activeSchedules.forEach {

        }
    }
}