package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import arrow.core.right
import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.DomainKoinTest
import co.softov.morestuff.android.domain.createScheduleUseCaseTest
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.ScheduleType
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


class CreateScheduleUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val timeManager = TimeManagerImpl()
    private val cancelActiveScheduleUseCase = mockk<CancelActiveScheduleUseCase>()

    private val createTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

    private val createScheduleUseCaseImpl = CreateScheduleUseCaseImpl(
        scheduleRepository,
        timeManager,
        cancelActiveScheduleUseCase
    )

    @Test
    fun `creates schedule`() = runBlocking {
        val taskId = 1L
        val localTime = timeManager.nowLocalDateTime
        val priority = Priority.Plan(localTime)
        val schedule = createScheduleUseCaseTest(
            createTime = createTime,
            scheduleTimeLocal = localTime.toString(),
            scheduleTimeUtc = timeManager.nowUtcInstantString,
            timeZone = TimeZone.currentSystemDefault().id
        )

        coEvery { cancelActiveScheduleUseCase(any()) } coAnswers { schedule.right() }

        coEvery { scheduleRepository.createSchedule(any()) } coAnswers {
            Either.Right(schedule)
        }

        val result = createScheduleUseCaseImpl.invoke(taskId, ScheduleType.OneTime, localTime)
        Assertions.assertEquals(Either.Right(schedule.copy(id = 1)), result)

        coVerify {
            cancelActiveScheduleUseCase(any())
            scheduleRepository.createSchedule(any())
        }

    }
}
