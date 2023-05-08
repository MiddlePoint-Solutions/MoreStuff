package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.DomainKoinTest
import co.softov.morestuff.android.domain.createScheduleUseCaseTest
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.koin.test.inject


class CreateScheduleUseCaseImplTest : DomainKoinTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val timeManager by inject<TimeManager>()

    private val createTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

    private val createScheduleUseCaseImpl = CreateScheduleUseCaseImpl(
        scheduleRepository,
        timeManager
    )

    @Test
    fun `creates schedule`() = runBlocking {
        val taskId = 1L
        val localTime = timeManager.nowLocalDateTimeString
        val priority = Priority.Plan(localTime)
        val schedule = createScheduleUseCaseTest(
            createTime = createTime,
            scheduleTimeLocal = localTime,
            scheduleTimeUtc = timeManager.nowUtcInstantString,
            timeZone = TimeZone.currentSystemDefault().id
        )

        coEvery { scheduleRepository.createSchedule(any()) } coAnswers {
            Either.Right(schedule)
        }

        val result = createScheduleUseCaseImpl.invoke(taskId, priority)
        Assertions.assertEquals(Either.Right(schedule.copy(id = 1)), result)
        coVerify { scheduleRepository.createSchedule(any()) }
    }
}
