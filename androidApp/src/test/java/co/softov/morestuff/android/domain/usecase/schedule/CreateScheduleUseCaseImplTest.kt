package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.createScheduleUseCaseTest
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.time.GetPriorityTimeUseCase
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test


class CreateScheduleUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val timeManager = mockk<TimeManager>()
    private val getPriorityTimeUseCase = mockk<GetPriorityTimeUseCase>()

    private val createTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

    private val createScheduleUseCaseImpl = CreateScheduleUseCaseImpl(
        scheduleRepository,
        getPriorityTimeUseCase,
        timeManager
    )

    @Test
    fun `creates schedule`() = runBlocking {
        val taskId = 1L
        val priority = Priority.today
        val localTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val schedule = createScheduleUseCaseTest(
            createTime = createTime,
            scheduleTimeLocal = localTime.toString(),
            scheduleTimeUtc = localTime.toInstant(TimeZone.UTC).toString(),
            timeZone = TimeZone.currentSystemDefault().id
        )

        every { timeManager.currentTimeZone } returns TimeZone.currentSystemDefault()
        every { getPriorityTimeUseCase(priority) } returns localTime.toString()
        every { timeManager.getCreateTime() } returns createTime
        every { timeManager.nowLocalDateTimeString } returns ""
        coEvery { scheduleRepository.createSchedule(any()) } coAnswers {
            val createdSchedule = arg<Schedule>(0).copy(id = 1)
            Either.Right(createdSchedule)
        }

        val result = createScheduleUseCaseImpl.invoke(taskId, priority)

        assertEquals(Either.Right(schedule.copy(id = 1)), result)
        verify { getPriorityTimeUseCase(priority) }
        coVerify { scheduleRepository.createSchedule(any()) }
    }
}
