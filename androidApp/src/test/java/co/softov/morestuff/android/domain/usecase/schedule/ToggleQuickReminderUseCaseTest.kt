package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import arrow.core.right
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.model.TaskReminderCancelled
import co.softov.morestuff.android.domain.usecase.task.GetTaskUseCase
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
            val schedule = ScheduleDomain(id = 1, scheduleType = ScheduleType.Reminder)
            val task = TaskDomain(
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

            val schedule = ScheduleDomain(id = 1, scheduleType = ScheduleType.Reminder)
            val task = TaskDomain(id = 1)

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