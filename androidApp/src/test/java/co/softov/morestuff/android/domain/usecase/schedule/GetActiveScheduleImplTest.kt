package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import arrow.core.right
import co.softov.morestuff.android.domain.createScheduleForTest
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetActiveScheduleImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getActiveScheduleImpl = GetActiveScheduleUseCaseImpl(scheduleRepository)

    @Test
    fun `returns active schedule for a task`() = runBlocking {
        val taskId = 1L
        val schedule = listOf(createScheduleForTest(scheduleType = ScheduleType.OneTime))

        coEvery {
            scheduleRepository.getActiveSchedulesForTask(
                taskId,
                listOf(ScheduleType.OneTime)
            )
        } returns schedule.right()

        val result = getActiveScheduleImpl(taskId, listOf(ScheduleType.OneTime))

        assertEquals(schedule.right(), result)
        coVerify { scheduleRepository.getActiveSchedulesForTask(taskId, any()) }
    }
}
