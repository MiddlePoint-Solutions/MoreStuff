package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.createScheduleWithTitleList
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetSchedulesWithTitleImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getSchedulesWithTitleImpl = GetSchedulesWithTitleImplUseCase(scheduleRepository)
    private val scheduleWithTitle = createScheduleWithTitleList(3)

    @Test
    fun `returns schedules with title`() = runBlocking {
        coEvery { scheduleRepository.getActiveSchedulesWithTitleFlow() }  returns flowOf (scheduleWithTitle)

        val result = getSchedulesWithTitleImpl.invoke().toList().flatten()

        assertEquals(scheduleWithTitle, result)
        coVerify { scheduleRepository.getActiveSchedulesWithTitleFlow() }
    }
}
