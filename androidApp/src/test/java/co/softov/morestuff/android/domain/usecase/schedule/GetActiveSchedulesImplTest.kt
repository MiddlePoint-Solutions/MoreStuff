package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.createListOfSchedulesForTest
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetActiveSchedulesImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getActiveSchedulesImpl = GetActiveSchedulesImpl(scheduleRepository)

    @Test
    fun `gets active schedules `() = runBlocking {
        val startTime = "12"
        val endTime = "13"
        val schedules = createListOfSchedulesForTest(2)

        coEvery { scheduleRepository.getActiveSchedules(startTime, endTime) } returns Either.Right(
            schedules)

        val result = getActiveSchedulesImpl.invoke(startTime, endTime)

        assertEquals(Either.Right(schedules), result)
        coVerify { scheduleRepository.getActiveSchedules(startTime, endTime) }
    }
}
