package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.createListOfSchedulesForTest
import co.softov.morestuff.android.domain.createScheduleWithTitle
import co.softov.morestuff.android.domain.createScheduleWithTitleList
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetLaterSchedulesWithTitleImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getLaterSchedulesWithTitleImpl = GetLaterSchedulesWithTitleImpl(scheduleRepository)
    private val scheduleWithTitle = createScheduleWithTitleList(2)



    @Test
    fun `returns later schedules with title`() = runBlocking {
        coEvery { scheduleRepository.getActiveLaterSchedulesWithTitleFlow() } returns flowOf(scheduleWithTitle)

        val result = getLaterSchedulesWithTitleImpl.invoke().toList().flatten()

        assertEquals(scheduleWithTitle, result)
        coVerify { scheduleRepository.getActiveLaterSchedulesWithTitleFlow() }
    }

}
