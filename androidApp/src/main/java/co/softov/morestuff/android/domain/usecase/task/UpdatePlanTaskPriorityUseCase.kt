package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository


interface UpdatePlanTaskPriorityUseCase {
    suspend operator fun invoke()
}

class UpdatePlanTaskPriorityUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val getPlanPriorityScoreUseCase: GetPlanPriorityScoreUseCase,
) : UpdatePlanTaskPriorityUseCase {

    override suspend fun invoke() {
        taskRepository.getActiveTasksWithScheduleFlow().collect { tasks ->
            for (task in tasks) {
                val scheduleDomain = task.activeSchedule
                if (scheduleDomain?.scheduleLocalTime != null) {
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