package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.service.TimeManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.max
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
    fun `test invoke`() = runBlocking {
        val scheduleTimeLocal = "2023-06-19T20:00:00Z"
        val taskCreateTimeUtc = "2023-06-19T10:00:00Z"

        val createTime = taskCreateTimeUtc.toInstant().epochSeconds
        val scheduleTime = scheduleTimeLocal.toInstant().epochSeconds
        val currentTime = "2023-06-19T15:00:00Z".toInstant().epochSeconds

        val lp: Long = 10
        val hp: Long = 100

        coEvery { getLowestPriorityScoreUseCase.invoke() } returns lp
        coEvery { getHighestPriorityScoreUseCase.invoke() } returns hp
        coEvery { timeManager.localDateTimeStringToUtc(scheduleTimeLocal) } returns scheduleTimeLocal.toInstant()
        coEvery { timeManager.nowUtcInstant } returns Instant.fromEpochSeconds(currentTime)

        val expectedScore = if (scheduleTime - createTime != 0L) {
            lp + (max(0, currentTime - createTime) / max(0, scheduleTime - createTime)) * (hp - lp)
        } else {
            lp
        }

        val actualScore = useCase.invoke(scheduleTimeLocal, taskCreateTimeUtc)

        assertEquals(expectedScore, actualScore)
    }}

