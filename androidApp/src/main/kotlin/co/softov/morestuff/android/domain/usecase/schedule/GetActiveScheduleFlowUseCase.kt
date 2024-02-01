package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetActiveScheduleFlowUseCase {
    operator fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType> = ScheduleType.values().asList()
    ): Flow<List<ScheduleDomain>>
}

class GetActiveScheduleFlowUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveScheduleFlowUseCase {
    override fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Flow<List<ScheduleDomain>> {
        return scheduleRepository.getActiveSchedulesForTaskFlow(listOf(taskId), scheduleType)
    }
}