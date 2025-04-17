package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.right
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.model.TaskReminderCancelled
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ToggleQuickReminderUseCaseTest {

    private val getTaskUseCase = mockk<GetTaskUseCase>(relaxed = true)
    private val createReminderUseCase = mockk<CreateReminderUseCase>(relaxed = true)
    private val cancelActiveScheduleUseCase = mockk<CancelActiveScheduleUseCase>(relaxed = true)

    @Test
    fun `Toggle quick reminder when task has reminder, cancels the reminder`() =
        runBlocking {
            val schedule = Schedule(id = 1, scheduleType = ScheduleType.Reminder)
            val task = Task(
                id = 1,
                schedule = listOf(schedule)
            )

            val toggleQuickReminderUseCase = ToggleQuickReminderUseCaseImpl(
                getTaskUseCase,
                createReminderUseCase,
                cancelActiveScheduleUseCase
            )

            coEvery { getTaskUseCase(any()) } returns task.right()
            coEvery {
              cancelActiveScheduleUseCase(listOf(task.id), any())
            } returns listOf(schedule).right()

            val result = toggleQuickReminderUseCase(task.id)

            coVerify {
                getTaskUseCase(task.id)
              cancelActiveScheduleUseCase(listOf(task.id), any())
            }

            assertTrue(result.isLeft { it is TaskReminderCancelled } )
        }

    @Test
    fun `Toggle quick reminder when task has no reminder, schedules the reminder`() =
        runBlocking {

            val schedule = Schedule(id = 1, scheduleType = ScheduleType.Reminder)
            val task = Task(id = 1)

            val toggleQuickReminderUseCase = ToggleQuickReminderUseCaseImpl(
                getTaskUseCase,
                createReminderUseCase,
                cancelActiveScheduleUseCase
            )

            coEvery { getTaskUseCase(task.id) } returns task.right()
            coEvery { createReminderUseCase(task.id) } returns schedule.right()

            val result = toggleQuickReminderUseCase(task.id)

            coVerify {
                getTaskUseCase(task.id)
                createReminderUseCase(task.id)
            }

            assertTrue(result.isRight { it == schedule } )
        }

}