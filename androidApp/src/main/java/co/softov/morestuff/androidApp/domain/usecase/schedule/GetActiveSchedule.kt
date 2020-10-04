package co.softov.morestuff.androidApp.domain.usecase.schedule


import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository


interface GetActiveSchedule {
    suspend operator fun invoke(taskId: Long): SimpleResult<Schedule>
}

class GetActiveScheduleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveSchedule {
    override suspend fun invoke(taskId: Long): SimpleResult<Schedule> {
        return scheduleRepository.getActiveScheduleForTask(taskId)
    }
} 



