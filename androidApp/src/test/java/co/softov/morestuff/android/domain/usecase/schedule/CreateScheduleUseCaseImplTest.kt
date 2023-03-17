package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.createScheduleForTest
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class CreateScheduleUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val timeManager = mockk<TimeManager>()
    private val createScheduleUseCaseImpl = CreateScheduleUseCaseImpl(
        scheduleRepository,
        timeManager
    )

    @Test
    fun `creates schedule`() = runBlocking {
        val taskId = 1L
        val priority = Priority.today
        val scheduleTimeLocal = TimeUtils.getCreateTime()

        val schedule = createScheduleForTest()

        coEvery { timeManager.getPriorityTime(priority) } returns scheduleTimeLocal
        coEvery { scheduleRepository.createSchedule(any()) } returns Either.Right(schedule)

        val result = createScheduleUseCaseImpl.invoke(taskId, priority)

        Assertions.assertEquals(Either.Right(schedule), result)
        coVerify { timeManager.getPriorityTime(priority) }
        coVerify { scheduleRepository.createSchedule(match { it.taskId == taskId && it.scheduleTimeLocal == scheduleTimeLocal && it.scheduleTimeUtc == null && it.active }) }
    }
}
