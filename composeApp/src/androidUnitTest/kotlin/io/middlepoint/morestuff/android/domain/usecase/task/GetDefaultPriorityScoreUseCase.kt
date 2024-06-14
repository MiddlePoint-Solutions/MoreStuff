package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.data.service.TimeManagerImpl
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetDefaultPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetHighestPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetLowestPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetPlanPriorityScoreUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GetDefaultPriorityScoreUseCaseTest {

    private val timeManager: TimeManager = TimeManagerImpl()
    private val getHighestPriorityScoreUseCase = mockk<GetHighestPriorityScoreUseCase>()
    private val getLowestPriorityScoreUseCase = mockk<GetLowestPriorityScoreUseCase>()
    private val getPlanPriorityScoreUseCase = GetPlanPriorityScoreUseCaseImpl(
        getLowestPriorityScoreUseCase = getLowestPriorityScoreUseCase,
        getHighestPriorityScoreUseCase = getHighestPriorityScoreUseCase,
        timeManager = timeManager
    )

    private val getDefaultPriorityScoreUseCase = GetDefaultPriorityScoreUseCaseImpl(
        getLowestPriorityScoreUseCase = getLowestPriorityScoreUseCase,
        getHighestPriorityScoreUseCase = getHighestPriorityScoreUseCase,
        getPlanPriorityScoreUseCase = getPlanPriorityScoreUseCase,
    )

    @Test
    fun `default NOW priority score should be highest + 1`() = runBlocking {
        coEvery { getHighestPriorityScoreUseCase() } returns 41
        val result = getDefaultPriorityScoreUseCase(Priority.Now())
        coVerify { getHighestPriorityScoreUseCase() }
        Assertions.assertEquals(42, result)
    }

    @Test
    fun `default LATER priority score should be Lowest - 1`() = runBlocking {
        coEvery { getLowestPriorityScoreUseCase() } returns 43
        val result = getDefaultPriorityScoreUseCase(Priority.Later())
        coVerify { getLowestPriorityScoreUseCase() }
        Assertions.assertEquals(42, result)
    }

    @Test
    fun `default PLAN priority score for should be the lowest score available`() = runBlocking {
        coEvery { getLowestPriorityScoreUseCase() } returns 42
        coEvery { getHighestPriorityScoreUseCase() } returns 84
        val scheduleLocalTime = timeManager.todayLocalDateTimeByAdding(hour = 1)
        val result = getDefaultPriorityScoreUseCase(Priority.Plan(scheduleLocalTime))
        Assertions.assertEquals(42, result)
    }

}

