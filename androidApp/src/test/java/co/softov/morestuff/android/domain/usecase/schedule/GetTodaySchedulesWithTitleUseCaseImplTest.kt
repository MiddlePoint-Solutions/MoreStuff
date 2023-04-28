package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import arrow.core.right
import co.softov.morestuff.android.data.service.TimeManagerImpl
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

class GetTodaySchedulesWithTitleUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()

    private val timeManager = TimeManagerImpl()
    private val getTodaySchedulesWithTitleImpl =
        GetTodaySchedulesWithTitleUseCaseImpl(scheduleRepository, timeManager)
    private val scheduleWithTitleList = createScheduleWithTitleList(3).right()

    @Test
    fun `returns today's schedules with title`() = runBlocking {
        coEvery {
            scheduleRepository.getActiveSchedulesWithTitleByTime(any(), any())
        } returns scheduleWithTitleList

        val result = getTodaySchedulesWithTitleImpl.invoke()

        assertEquals(scheduleWithTitleList, result)
        coVerify { scheduleRepository.getActiveSchedulesWithTitleByTime(any(), any()) }
    }
}
