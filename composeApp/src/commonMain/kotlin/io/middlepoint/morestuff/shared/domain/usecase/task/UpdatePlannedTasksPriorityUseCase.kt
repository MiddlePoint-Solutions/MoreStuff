package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetPlanPriorityScoreUseCase
import kotlinx.datetime.LocalDateTime

interface UpdatePlannedTasksPriorityUseCase {
    suspend operator fun invoke()
}

class UpdatePlannedTasksPriorityUseCaseImpl(
    private val getActiveTasksWithScheduleUseCase: GetActiveTasksWithScheduleUseCase,
    private val getPlanPriorityScoreUseCase: GetPlanPriorityScoreUseCase,
    private val updateTaskPriorityScoreUseCase: UpdateTaskPriorityScoreUseCase,
) : UpdatePlannedTasksPriorityUseCase {

    override suspend fun invoke() {
        getActiveTasksWithScheduleUseCase(listOf(ScheduleType.OneTime)).map { tasks ->
            tasks.forEach { task ->
                task.getScheduleOrNull()?.scheduleLocalTime?.let { scheduleLocalTime ->
                    val newPriorityScore = getPlanPriorityScoreUseCase(
                        scheduleTimeLocal = LocalDateTime.parse(scheduleLocalTime),
                        taskCreateTimeUtc = task.createdAt
                    )
                  updateTaskPriorityScoreUseCase(task.id, newPriorityScore)
                }
            }
        }
    }
}
