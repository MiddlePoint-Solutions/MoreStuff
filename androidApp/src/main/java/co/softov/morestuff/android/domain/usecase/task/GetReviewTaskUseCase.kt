package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetReviewTaskUseCase {
    suspend operator fun invoke(): Flow<List<TaskDomain>>
}

class GetReviewTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val scheduleRepository: ScheduleRepository,
) : GetReviewTaskUseCase {

    override suspend fun invoke(): Flow<List<TaskDomain>> =
        taskRepository.getActiveTasksFlow()
            .map { tasks ->
                tasks.filterNot { task ->
                    scheduleRepository.taskHasSchedule(task.id)
                }
            }
}
