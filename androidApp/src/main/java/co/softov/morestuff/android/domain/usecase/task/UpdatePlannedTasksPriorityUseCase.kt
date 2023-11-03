package co.softov.morestuff.android.domain.usecase.task

import kotlinx.datetime.toLocalDateTime
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.usecase.priority.GetPlanPriorityScoreUseCase
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
        getActiveTasksWithScheduleUseCase(listOf(ScheduleType.OneTime)).map { tasks ->
            Timber.d("UpdatePlannedTasksPriorityUseCase tasks: ${tasks.size}")
            tasks.forEach { task ->
                Timber.d("UpdatePlannedTasksPriorityUseCase task: ${task.title}")
                task.getScheduleOrNull()?.scheduleLocalTime?.let { scheduleLocalTime ->
                    val newPriorityScore = getPlanPriorityScoreUseCase(
                        scheduleTimeLocal = scheduleLocalTime.toLocalDateTime(),
                        taskCreateTimeUtc = task.createTime
                    )
                    updateTaskPriorityScoreUseCase(task.id, newPriorityScore)
                }
            }
        }
    }
}
