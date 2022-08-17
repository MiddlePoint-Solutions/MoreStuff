package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.Scope
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase

interface GetScheduleTaskUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure,Scope>
}

class GetScheduleTaskUseCaseImpl(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : GetScheduleTaskUseCase {
    override suspend fun invoke(scheduleId: Long): Either<Failure,Scope> {
        return when (val result = getScheduleUseCase(scheduleId)) {
            is Either.Right -> getTaskUseCase(result.value.taskId)
            is Either.Left -> result
        }
    }
}