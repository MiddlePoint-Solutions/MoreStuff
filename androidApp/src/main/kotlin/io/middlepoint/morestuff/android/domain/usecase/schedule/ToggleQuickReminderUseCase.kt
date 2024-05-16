package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import arrow.core.flatMap
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScheduleDomain
import io.middlepoint.morestuff.android.domain.service.TimeManager
import io.middlepoint.morestuff.android.domain.enums.ScheduleType
import io.middlepoint.morestuff.android.domain.model.TaskReminderCancelled
import io.middlepoint.morestuff.android.domain.usecase.task.GetTaskUseCase

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