package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.repository.TaskRepository


interface UpdatePlanTaskPriorityUseCase {
    suspend operator fun invoke()
}

class UpdatePlanTaskPriorityUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val scheduleRepository: ScheduleRepository,
    private val getPlanPriorityScoreUseCase: GetPlanPriorityScoreUseCase
) : UpdatePlanTaskPriorityUseCase {

    override suspend fun invoke() {
        taskRepository.getActiveTasksFlow().collect { tasks ->
            for (task in tasks) {
                val schedule = scheduleRepository.getActiveScheduleForTask(task.id)
                if (schedule is Either.Right) {
                    val scheduleDomain = schedule.value
                    val newPriorityScore = getPlanPriorityScoreUseCase(
                        scheduleTimeLocal = scheduleDomain.scheduleLocalTime.toString(),
                        taskCreateTimeUtc = task.createTime
                    )
                    taskRepository.updateTaskPriority(task.id, newPriorityScore)
                }
            }
        }
    }
}

