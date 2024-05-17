package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.createScheduleForTest
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetScheduleImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getScheduleImpl = GetScheduleImpl(scheduleRepository)
    private val schedule =
      io.middlepoint.morestuff.android.domain.createScheduleForTest(scheduleType = ScheduleType.OneTime)

    @Test
    fun `returns schedule`() = runBlocking {
        coEvery { scheduleRepository.getSchedule(schedule.id) } returns Either.Right(schedule)

        val result = getScheduleImpl.invoke(schedule.id)

        assertEquals(Either.Right(schedule), result)
        coVerify { scheduleRepository.getSchedule(schedule.id) }
    }
}
