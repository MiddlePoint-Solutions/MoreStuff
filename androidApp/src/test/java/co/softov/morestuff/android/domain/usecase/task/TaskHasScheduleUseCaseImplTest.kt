package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TaskHasScheduleUseCaseTest {
    @Test
    fun ` return true when task has a schedule`() {

        val scheduleRepository = mockk<ScheduleRepository>()
        val useCase = TaskHasScheduleUseCaseImpl(scheduleRepository)

        val taskId = 1L
        coEvery { scheduleRepository.taskHasSchedule(taskId) } returns true

        val result = runBlocking { useCase.invoke(taskId) }

        assertEquals(true, result)
    }
}