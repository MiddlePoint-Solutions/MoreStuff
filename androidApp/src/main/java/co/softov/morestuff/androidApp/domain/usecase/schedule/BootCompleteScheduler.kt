package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.data.utils.TimeUtils
import co.softov.morestuff.androidApp.data.utils.scheduleLocalDateTime
import co.softov.morestuff.androidApp.domain.service.Scheduler
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import timber.log.Timber

interface BootCompleteScheduler {
    suspend operator fun invoke(): SimpleResult<Boolean>
}

class BootCompleteSchedulerImpl(
    private val getActiveSchedules: GetActiveSchedules,
    private val scheduler: Scheduler
) : BootCompleteScheduler {

    override suspend fun invoke(): SimpleResult<Boolean> {
        return when (val result = getActiveSchedules()) {
            is Result.Failure -> result
            is Result.Success -> {
                reschedule(result.value.toMutableList())
                Result.Success(true)
            }
        }
    }

    private fun reschedule(activeSchedules: MutableList<Schedule>) {
        val currentTime = TimeUtils.currentLocalDateTime
        Timber.d("BootComplete, Current time: $currentTime")
        Timber.d("BootComplete, active schedules: ${activeSchedules.size}")

        val futureSchedules = activeSchedules.filter { schedule ->
            schedule.scheduleLocalDateTime?.let { it > currentTime } ?: false
        }

        Timber.d("BootComplete, rescheduling future tasks: ${futureSchedules.size}")
        futureSchedules.forEach { schedule ->
            schedule.scheduleTime?.let { time ->
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