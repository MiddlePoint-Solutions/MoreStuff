package co.softov.morestuff.android.domain.usecase.task


interface UpdatePlannedTasksPriorityUseCase {
    suspend operator fun invoke()
}

class UpdatePlannedTasksPriorityUseCaseImpl(
    private val getActiveTasksWithScheduleUseCase: GetActiveTasksWithScheduleUseCase,
    private val getPlanPriorityScoreUseCase: GetPlanPriorityScoreUseCase,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
) : UpdatePlannedTasksPriorityUseCase {

    override suspend fun invoke() {
        getActiveTasksWithScheduleUseCase().collect { tasks ->
            for (task in tasks) {
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
