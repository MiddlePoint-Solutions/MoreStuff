package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.createScheduleForTest
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetActiveScheduleForTaskUseCaseTest {
    @Test
    fun ` return schedule domain when schedule exists`() {

        val scheduleRepository = mockk<ScheduleRepository>()
        val useCase = GetActiveScheduleForTaskUseCaseImpl(scheduleRepository)

        val taskId = 12L
        val scheduleDomain = createScheduleForTest()
        coEvery { scheduleRepository.getActiveScheduleForTask(taskId) } returns Either.Right(
            scheduleDomain
        )

        val result = runBlocking { useCase.invoke(taskId) }

        assertEquals(Either.Right(scheduleDomain), result)
    }
}