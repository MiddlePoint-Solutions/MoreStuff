package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.android.domain.service.TimeManager
import io.middlepoint.morestuff.android.domain.usecase.priority.GetHighestPriorityScoreUseCase
import io.middlepoint.morestuff.android.domain.usecase.priority.GetLowestPriorityScoreUseCase
import io.middlepoint.morestuff.android.domain.usecase.priority.GetPlanPriorityScoreUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
class GetPlanPriorityScoreUseCaseImplTest {

    private val getLowestPriorityScoreUseCase: GetLowestPriorityScoreUseCase = mockk()
    private val getHighestPriorityScoreUseCase: GetHighestPriorityScoreUseCase = mockk()
    private val timeManager: TimeManager = mockk()

    private val useCase = GetPlanPriorityScoreUseCaseImpl(
        getLowestPriorityScoreUseCase,
        getHighestPriorityScoreUseCase,
        timeManager
    )

    @Test
    fun `calculate priority for plan task with positive priority`() = runBlocking {
        val taskCreateTimeUtc = "2023-06-19T10:00:00Z"
        val scheduleTimeUTC = "2023-06-19T20:00:00Z".toInstant()
        val scheduleTimeLocal = "2023-06-19T20:00:00".toLocalDateTime()
        val currentTime = "2023-06-19T15:00:00Z"

        val lp: Long = 10
        val hp: Long = 100

        val expectedScore: Long = 55

        coEvery { getLowestPriorityScoreUseCase() } returns lp
        coEvery { getHighestPriorityScoreUseCase() } returns hp
        coEvery { timeManager.localDateTimeToUtc(scheduleTimeLocal) } returns scheduleTimeUTC
        coEvery { timeManager.nowUtcInstant } returns Instant.fromEpochSeconds(currentTime.toInstant().epochSeconds)

        val actualScore = useCase.invoke(scheduleTimeLocal, taskCreateTimeUtc)

        assertEquals(expectedScore, actualScore)
    }

    @Test
    fun `calculate priority for plan task with negative priority`() = runBlocking {
        val taskCreateTimeUtc = "2023-06-19T10:00:00Z"
        val scheduleTimeUTC = "2023-06-19T20:00:00Z".toInstant()
        val scheduleTimeLocal = "2023-06-19T20:00:00".toLocalDateTime()
        val currentTime = "2023-06-19T15:00:00Z"

        val lp: Long = -10
        val hp: Long = -100

        val expectedScore: Long = -55

        coEvery { getLowestPriorityScoreUseCase() } returns lp
        coEvery { getHighestPriorityScoreUseCase() } returns hp
        coEvery { timeManager.localDateTimeToUtc(scheduleTimeLocal) } returns scheduleTimeUTC
        coEvery { timeManager.nowUtcInstant } returns Instant.fromEpochSeconds(currentTime.toInstant().epochSeconds)

        val actualScore = useCase.invoke(scheduleTimeLocal, taskCreateTimeUtc)

        assertEquals(expectedScore, actualScore)
    }

    @Test
    fun `calculate priority for plan task with negative and positive priority`() = runBlocking {
        val taskCreateTimeUtc = "2023-06-19T10:00:00Z"
        val scheduleTimeUTC = "2023-06-19T20:00:00Z".toInstant()
        val scheduleTimeLocal = "2023-06-19T20:00:00".toLocalDateTime()
        val currentTime = "2023-06-19T15:00:00Z"

        val lp: Long = 77
        val hp: Long = -103

        val expectedScore: Long = -13

        coEvery { getLowestPriorityScoreUseCase() } returns lp
        coEvery { getHighestPriorityScoreUseCase() } returns hp
        coEvery { timeManager.localDateTimeToUtc(scheduleTimeLocal) } returns scheduleTimeUTC
        coEvery { timeManager.nowUtcInstant } returns Instant.fromEpochSeconds(currentTime.toInstant().epochSeconds)

        val actualScore = useCase.invoke(scheduleTimeLocal, taskCreateTimeUtc)

        assertEquals(expectedScore, actualScore)
    }
}

