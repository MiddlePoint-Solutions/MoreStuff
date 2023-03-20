package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import arrow.core.getOrHandle
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.di.useCaseModules
import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.TimeOfDayOption
import co.softov.morestuff.android.domain.repository.ScheduleDoesNotExist
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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

    private val getActiveScheduleFlowUseCase: GetActiveScheduleFlowUseCase = mockk()
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
                getActiveScheduleFlow = getActiveScheduleFlowUseCase,
                mapScheduleToPriority = get(),
                getPriorityOptionsUseCase = get(),
                getUpcomingPriorityUseCase = getUpcomingPriorityUseCase
            )

            coEvery { getActiveScheduleFlowUseCase(taskId) } returns flowOf(Either.Right(schedule))
            coVerify(inverse = true) { getUpcomingPriorityUseCase(GetUpcomingPriorityParams()) }

            val result = useCase(params).first().getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.today.copy(option = TimeOfDayOption.Morning), result.model.priority)
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
                getActiveScheduleFlow = getActiveScheduleFlowUseCase,
                mapScheduleToPriority = get(),
                getPriorityOptionsUseCase = get(),
                getUpcomingPriorityUseCase = getUpcomingPriorityUseCase
            )

            coEvery { getActiveScheduleFlowUseCase(taskId) } returns flowOf(Either.Right(schedule))
            coVerify(inverse = true) { getUpcomingPriorityUseCase(GetUpcomingPriorityParams()) }

            val result = useCase(params).first().getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.tomorrow.copy(option = TimeOfDayOption.Morning), result.model.priority)
        }

    @Test
    fun `should return upcoming priority when no schedule available`() =
        runBlocking {
            val taskId = 1L
            val params = GetSchedulePriorityParams(taskId)
            val priorityParams = GetUpcomingPriorityParams()

            val useCase = GetSchedulePriorityUseCaseImpl(
                getActiveScheduleFlow = getActiveScheduleFlowUseCase,
                mapScheduleToPriority = get(),
                getPriorityOptionsUseCase = get(),
                getUpcomingPriorityUseCase = getUpcomingPriorityUseCase
            )

            coEvery { getActiveScheduleFlowUseCase(taskId) } returns flowOf(
                Either.Left(
                    ScheduleDoesNotExist
                )
            )
            coEvery { getUpcomingPriorityUseCase(priorityParams) } returns Either.Right(
                Priority.today.copy(option = TimeOfDayOption.Afternoon)
            )

            val result = useCase(params).first().getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.today.copy(option = TimeOfDayOption.Afternoon), result.model.priority)
        }

    @Test
    fun `should return Today auto priority when scheduled for today out of range`() =
        runBlocking {
            val taskId = 1L
            val params = GetSchedulePriorityParams(taskId)
            val schedule = Schedule.empty().copy(
                scheduleLocalTime = TimeUtils.todayLocalDateTimeString(21)
            )

            val useCase = GetSchedulePriorityUseCaseImpl(
                getActiveScheduleFlow = getActiveScheduleFlowUseCase,
                mapScheduleToPriority = get(),
                getPriorityOptionsUseCase = get(),
                getUpcomingPriorityUseCase = getUpcomingPriorityUseCase
            )

            coEvery { getActiveScheduleFlowUseCase(taskId) } returns flowOf(Either.Right(schedule))
            coVerify(inverse = true) { getUpcomingPriorityUseCase(GetUpcomingPriorityParams()) }

            val result = useCase(params).first().getOrHandle { throw (Throwable(it.toString())) }
            assertEquals(Priority.today.copy(option = DefaultOption.Auto), result.model.priority)
        }


}

