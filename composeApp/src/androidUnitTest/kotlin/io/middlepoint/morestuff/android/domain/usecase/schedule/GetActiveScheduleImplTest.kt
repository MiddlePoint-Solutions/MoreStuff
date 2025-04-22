package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.right
import io.middlepoint.morestuff.android.domain.createScheduleForTest
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetActiveScheduleImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getActiveScheduleImpl = GetActiveSchedulesUseCaseImpl(scheduleRepository)

    @Test
    fun `returns active schedule for a task`() = runBlocking {
        val taskId = listOf(Uuid("1"))
        val schedule = listOf(
          createScheduleForTest(
            scheduleType = ScheduleType.OneTime
          )
        )

        coEvery {
            scheduleRepository.getActiveSchedulesForTasks(
                taskId,
                listOf(ScheduleType.OneTime)
            )
        } returns schedule.right()

        val result = getActiveScheduleImpl.invoke(taskId, listOf(ScheduleType.OneTime))

        assertEquals(schedule.right(), result)
        coVerify { scheduleRepository.getActiveSchedulesForTasks(taskId, any()) }
    }
}
