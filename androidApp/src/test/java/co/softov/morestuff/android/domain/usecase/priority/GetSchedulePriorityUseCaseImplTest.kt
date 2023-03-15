package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import arrow.core.getOrHandle
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.di.useCaseModules
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.TimeOfDayOption
import co.softov.morestuff.android.domain.repository.ScheduleDoesNotExist
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

internal class GetSchedulePriorityUseCaseImplTest : KoinTest {

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            startKoin {
                modules(useCaseModules)
            }
        }

        @AfterAll
        fun cleanup() {
            stopKoin()
        }

    }

    private val getActiveSchedule: GetActiveScheduleUseCase = mockk()
    private val getUpcomingPriorityUseCase: GetUpcomingPriorityUseCase = mockk()

    @Test
    fun `should return Today morning priority when scheduled for today morning`() =
        runBlocking {
            val taskId = 1L
            val params = GetSchedulePriorityParams(taskId)
            val schedule = Schedule.empty().copy(
                scheduleLocalTime = TimeUtils.todayLocalDateTimeString(9)
            )

            val useCase = GetSchedulePriorityUseCaseImpl(
                getActiveSchedule = getActiveSchedule,
                mapScheduleToPriority = get(),
                getPriorityOptionsUseCase = get(),
                getUpcomingPriorityUseCase = getUpcomingPriorityUseCase
            )

            coEvery { getActiveSchedule(taskId) } returns Either.Right(schedule)
            coVerify(inverse = true) { getUpcomingPriorityUseCase(GetUpcomingPriorityParams()) }

            val result = useCase(params).getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.today.copy(option = TimeOfDayOption.Morning), result.priority)
        }

    @Test
    fun `should return Tomorrow morning priority when scheduled for tomorrow morning`() =
        runBlocking {
            val taskId = 1L
            val params = GetSchedulePriorityParams(taskId)
            val schedule = Schedule.empty().copy(
                scheduleLocalTime = TimeUtils.tomorrowLocalDateTimeString(8)
            )

            val useCase = GetSchedulePriorityUseCaseImpl(
                getActiveSchedule = getActiveSchedule,
                mapScheduleToPriority = get(),
                getPriorityOptionsUseCase = get(),
                getUpcomingPriorityUseCase = getUpcomingPriorityUseCase
            )

            coEvery { getActiveSchedule(taskId) } returns Either.Right(schedule)
            coVerify(inverse = true) { getUpcomingPriorityUseCase(GetUpcomingPriorityParams()) }

            val result = useCase(params).getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.tomorrow.copy(option = TimeOfDayOption.Morning), result.priority)
        }

    @Test
    fun `should return upcoming priority when no schedule available`() =
        runBlocking {
            val taskId = 1L
            val params = GetSchedulePriorityParams(taskId)
            val priorityParams = GetUpcomingPriorityParams()

            val useCase = GetSchedulePriorityUseCaseImpl(
                getActiveSchedule = getActiveSchedule,
                mapScheduleToPriority = get(),
                getPriorityOptionsUseCase = get(),
                getUpcomingPriorityUseCase = getUpcomingPriorityUseCase
            )

            coEvery { getActiveSchedule(taskId) } returns Either.Left(ScheduleDoesNotExist)
            coEvery { getUpcomingPriorityUseCase(priorityParams) } returns Either.Right(
                Priority.today.copy(option = TimeOfDayOption.Afternoon)
            )

            val result = useCase(params).getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.today.copy(option = TimeOfDayOption.Afternoon), result.priority)
        }


}

