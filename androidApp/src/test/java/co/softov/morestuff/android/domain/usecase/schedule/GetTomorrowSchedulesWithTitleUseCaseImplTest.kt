package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.createScheduleWithTitleList
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetTomorrowSchedulesWithTitleUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getTomorrowSchedulesWithTitleImpl = GetTomorrowSchedulesWithTitleUseCaseImpl(scheduleRepository)
    private val scheduleWithTitleList = createScheduleWithTitleList(3)

    @Test
    fun `returns tomorrow schedules with title`() = runBlocking {
        coEvery { scheduleRepository.getTomorrowActiveSchedulesWithTitleFlow() } returns flowOf(scheduleWithTitleList)

        val result = getTomorrowSchedulesWithTitleImpl.invoke().first()

        assertEquals(scheduleWithTitleList, result)
        coVerify { scheduleRepository.getTomorrowActiveSchedulesWithTitleFlow() }
    }
}
