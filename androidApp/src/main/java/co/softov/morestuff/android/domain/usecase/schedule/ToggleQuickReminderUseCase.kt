package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import arrow.core.flatMap
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.TaskReminderCancelled
import co.softov.morestuff.android.domain.usecase.task.GetTaskUseCase

interface ToggleQuickReminderUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, ScheduleDomain>
}

class ToggleQuickReminderUseCaseImpl(
    private val getTaskUseCase: GetTaskUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase
) : ToggleQuickReminderUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure, ScheduleDomain> =
        getTaskUseCase(taskId).flatMap {
            if (it.hasReminder) {
                cancelActiveScheduleUseCase(listOf(taskId), listOf(ScheduleType.Reminder))
                Either.Left(TaskReminderCancelled)
            } else {
                createReminderUseCase(taskId)
            }
        }
}