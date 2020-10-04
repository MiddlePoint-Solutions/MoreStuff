package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.Scheduler
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import timber.log.Timber
import java.util.Calendar

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
                reschedule(result.value)
                Result.Success(true)
            }
        }
    }

    private fun reschedule(activeSchedules: List<Schedule>) {
        val currentTime = Calendar.getInstance().timeInMillis

        Timber.d("execute, active schedules: ${activeSchedules.size}")

        val futureSchedules = activeSchedules.filter { it.scheduleTime > currentTime }
        Timber.d("execute, future schedules: ${futureSchedules.size}")
        futureSchedules.forEach {
            scheduler.scheduleAtExact(it.id, it.scheduleTime)
        }

        // TODO: Get missed schedules that were not fulfilled and create reminders for them
        // TODO: should update the current schedule or create a new one?
        // TODO: How are we going to display this?
        //  What if there is a schedule that should show now and missed schedules?
        //  The priority goes to the current scheduled reminder and the missed schedules should be shown at a different time.
        val pastSchedules = activeSchedules.filter { it.scheduleTime <= currentTime }
        Timber.d("execute, missed schedules: ${pastSchedules.size}")
        pastSchedules.forEach {

        }
    }
}