package io.middlepoint.morestuff.shared.domain.usecase.schedule

import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
import kotlinx.coroutines.flow.Flow
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.map

interface GetTaskSchedulesUseCase {
  operator fun invoke(): Flow<Map<Long, ScheduleDomain>>
}

class GetTaskSchedulesUseCaseImpl(
  private val scheduleRepository: ScheduleRepository
) : GetTaskSchedulesUseCase {
  override fun invoke(): Flow<Map<Long, ScheduleDomain>> {
    return scheduleRepository.getActiveSchedulesFlow().map { schedules ->
      schedules.groupBy { it.taskId }.mapValues { it.value.first() }
    }
  }
}
