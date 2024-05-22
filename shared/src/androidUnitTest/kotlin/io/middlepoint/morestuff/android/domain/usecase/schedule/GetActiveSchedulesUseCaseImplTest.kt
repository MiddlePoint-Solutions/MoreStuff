package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.createListOfSchedulesForTest
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetActiveSchedulesUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getActiveSchedulesImpl = GetAllActiveSchedulesUseCaseImpl(scheduleRepository)

    @Test
    fun `gets active schedules `() = runBlocking {
        val schedules = io.middlepoint.morestuff.shared.domain.createListOfSchedulesForTest(2)

        coEvery { scheduleRepository.getActiveSchedules() } returns Either.Right(
            schedules
        )

        val result = getActiveSchedulesImpl.invoke()

        assertEquals(Either.Right(schedules), result)
        coVerify { scheduleRepository.getActiveSchedules() }
    }
}
