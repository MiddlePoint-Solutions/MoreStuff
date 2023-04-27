package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.createScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetScheduleWithTitleUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getScheduleWithTitleImpl = GetScheduleWithTitleUseCaseImpl(scheduleRepository)
    private val schedule = createScheduleWithTitle(3)
    @Test
    fun `returns schedule with title`() = runBlocking {
        coEvery { scheduleRepository.getActiveScheduleWithTitle(schedule.scheduleId) } returns Either.Right(schedule)

        val result = getScheduleWithTitleImpl.invoke(schedule.scheduleId)

        assertEquals(Either.Right(schedule), result)
        coVerify { scheduleRepository.getActiveScheduleWithTitle(schedule.scheduleId) }
    }
}
