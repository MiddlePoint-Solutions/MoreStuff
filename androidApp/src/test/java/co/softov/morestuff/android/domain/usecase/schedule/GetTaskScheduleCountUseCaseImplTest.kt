package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.usecase.time.GetPriorityTimeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetTaskScheduleCountUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val getPriorityTimeUseCase = mockk<GetPriorityTimeUseCase>()
    private val getTaskScheduleCountUseCaseImpl =
        GetTaskScheduleCountUseCaseImpl(scheduleRepository, getPriorityTimeUseCase)
    private val taskId = 1L
    private val timeRange = Pair("", "")

    @Test
    fun `returns count of task schedules`() = runBlocking {
        coEvery { getPriorityTimeUseCase.getTodayTimeRange() } returns timeRange
        coEvery {
            scheduleRepository.countTodayTaskSchedules(
                taskId,
                timeRange.first,
                timeRange.second
            )
        } returns Either.Right(2)
        val result = getTaskScheduleCountUseCaseImpl.invoke(taskId)

        assertEquals(Either.Right(2), result)
        coVerify { getPriorityTimeUseCase.getTodayTimeRange() }
        coVerify {
            scheduleRepository.countTodayTaskSchedules(
                taskId,
                timeRange.first,
                timeRange.second
            )
        }
    }
}