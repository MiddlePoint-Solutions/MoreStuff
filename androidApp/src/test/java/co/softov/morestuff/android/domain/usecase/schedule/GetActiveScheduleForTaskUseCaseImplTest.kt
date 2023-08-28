package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.right
import co.softov.morestuff.android.domain.createScheduleForTest
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetActiveScheduleForTaskUseCaseTest {
    @Test
    fun ` return schedule when schedule exists`() {

        val scheduleRepository = mockk<ScheduleRepository>()
        val useCase = GetActiveScheduleForTaskUseCaseImpl(scheduleRepository)

        val taskId = 12L
        val schedule = listOf(createScheduleForTest(scheduleType = ScheduleType.OneTime))
        coEvery {
            scheduleRepository.getActiveSchedulesForTask(
                taskId,
                listOf(ScheduleType.OneTime)
            )
        } returns schedule.right()

        val result = runBlocking { useCase(taskId, listOf(ScheduleType.OneTime)) }

        assertEquals(schedule.right(), result)
    }
}