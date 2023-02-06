package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.createScheduleForTest
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetActiveScheduleImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getActiveScheduleImpl = GetActiveScheduleImpl(scheduleRepository)

    @Test
    fun `returns active schedule for a task`() = runBlocking {
        val taskId = 1L
        val schedule = createScheduleForTest()

        coEvery { scheduleRepository.getActiveScheduleForTask(taskId) } returns Either.Right(schedule)

        val result = getActiveScheduleImpl.invoke(taskId)

        assertEquals(Either.Right(schedule), result)
        coVerify { scheduleRepository.getActiveScheduleForTask(taskId) }
    }
}
