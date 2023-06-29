package co.softov.morestuff.android.domain.usecase.task

import timber.log.Timber


interface UpdatePlannedTasksPriorityUseCase {
    suspend operator fun invoke()
}

class UpdatePlannedTasksPriorityUseCaseImpl(
    private val getActiveTasksWithScheduleUseCase: GetActiveTasksWithScheduleUseCase,
    private val getPlanPriorityScoreUseCase: GetPlanPriorityScoreUseCase,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
) : UpdatePlannedTasksPriorityUseCase {

    override suspend fun invoke() {
        Timber.d("UpdatePlannedTasksPriorityUseCase")
        getActiveTasksWithScheduleUseCase().map { tasks ->
            Timber.d("UpdatePlannedTasksPriorityUseCase tasks: ${tasks.size}")
            for (task in tasks) {
                Timber.d("UpdatePlannedTasksPriorityUseCase task: ${task.title}")
                val scheduleDomain = task.activeSchedule
                if (scheduleDomain?.scheduleLocalTime != null) {
                    val newPriorityScore = getPlanPriorityScoreUseCase(
                        scheduleTimeLocal = scheduleDomain.scheduleLocalTime.toString(),
                        taskCreateTimeUtc = task.createTime
                    )
                    updateTaskPriorityScoreUseCase(task.id, newPriorityScore)
                }
            }
        }
    }
}
