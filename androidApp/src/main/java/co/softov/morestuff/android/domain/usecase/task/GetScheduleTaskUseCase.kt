package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.Result
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase

interface GetScheduleTaskUseCase {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<Task>
}

class GetScheduleTaskUseCaseImpl(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : GetScheduleTaskUseCase {
    override suspend fun invoke(scheduleId: Long): SimpleResult<Task> {
        return when (val result = getScheduleUseCase(scheduleId)) {
            is Result.Success -> getTaskUseCase(result.value.taskId)
            is Result.Failure -> result
        }
    }
}