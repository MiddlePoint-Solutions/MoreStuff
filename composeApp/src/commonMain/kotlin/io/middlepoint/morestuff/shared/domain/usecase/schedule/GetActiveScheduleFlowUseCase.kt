package io.middlepoint.morestuff.shared.domain.usecase.schedule

import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetActiveScheduleFlowUseCase {
    operator fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType> = ScheduleType.entries
    ): Flow<List<Schedule>>

}

class GetActiveScheduleFlowUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveScheduleFlowUseCase {
    override fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Flow<List<Schedule>> {
        return scheduleRepository.getActiveSchedulesForTaskFlow(listOf(taskId), scheduleType)
    }
}