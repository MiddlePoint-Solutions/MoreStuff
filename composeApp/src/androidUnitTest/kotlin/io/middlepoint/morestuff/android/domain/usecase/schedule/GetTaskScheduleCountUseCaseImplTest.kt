package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetTaskScheduleCountUseCaseImplTest {
    private val scheduleRepository = mockk<ScheduleRepository>()
    private val timeManager = mockk<TimeManager>()
    private val getTaskScheduleCountUseCaseImpl =
        GetTaskScheduleCountUseCaseImpl(scheduleRepository, timeManager)
    private val taskId = 1L
    private val timeRange = Pair("", "")

    @Test
    fun `returns count of task schedules`() = runBlocking {
        coEvery { timeManager.getTodayTimeRange() } returns timeRange
        coEvery {
            scheduleRepository.countTodayTaskSchedules(
                taskId,
                timeRange.first,
                timeRange.second
            )
        } returns Either.Right(2)
        val result = getTaskScheduleCountUseCaseImpl.invoke(taskId)

        assertEquals(Either.Right(2), result)
        coVerify { timeManager.getTodayTimeRange() }
        coVerify {
            scheduleRepository.countTodayTaskSchedules(
                taskId,
                timeRange.first,
                timeRange.second
            )
        }
    }
}