package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.createScheduleWithTitleList
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetLaterSchedulesWithTitleUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getLaterSchedulesWithTitleImpl = GetLaterSchedulesWithTitleUseCaseImpl(scheduleRepository)
    private val scheduleWithTitle = createScheduleWithTitleList(2)



    @Test
    fun `returns later schedules with title`() = runBlocking {
        coEvery { scheduleRepository.getLaterActiveSchedulesWithTitleFlow() } returns flowOf(scheduleWithTitle)

        val result = getLaterSchedulesWithTitleImpl.invoke().toList().flatten()

        assertEquals(scheduleWithTitle, result)
        coVerify { scheduleRepository.getLaterActiveSchedulesWithTitleFlow() }
    }

}
