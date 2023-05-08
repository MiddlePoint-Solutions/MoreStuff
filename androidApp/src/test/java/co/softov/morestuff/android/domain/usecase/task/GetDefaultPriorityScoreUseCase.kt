package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.model.Priority
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GetDefaultPriorityScoreUseCaseTest {

    private val getHighestPriorityScoreUseCase = mockk<GetHighestPriorityScoreUseCase>()
    private val getLowestPriorityScoreUseCase = mockk<GetLowestPriorityScoreUseCase>()
    private val getPlanPriorityScoreUseCase = GetPlanPriorityScoreUseCaseImpl(
        getLowestPriorityScoreUseCase = getLowestPriorityScoreUseCase,
        getHighestPriorityScoreUseCase = getHighestPriorityScoreUseCase,
        timeManager = TimeManagerImpl()
    )

    private val getDefaultPriorityScoreUseCase = GetDefaultPriorityScoreUseCaseImpl(
        getLowestPriorityScoreUseCase = getLowestPriorityScoreUseCase,
        getHighestPriorityScoreUseCase = getHighestPriorityScoreUseCase,
        getPlanPriorityScoreUseCase = getPlanPriorityScoreUseCase,
    )

    @Test
    fun `default "Now" priority score should be highest + 1`() = runBlocking {
        coEvery { getHighestPriorityScoreUseCase() } returns 41
        val result = getDefaultPriorityScoreUseCase(Priority.Now())
        coVerify { getHighestPriorityScoreUseCase() }
        Assertions.assertEquals(42, result)
    }

    @Test
    fun `default "Later" priority score should be Lowest - 1`() = runBlocking {
        coEvery { getLowestPriorityScoreUseCase() } returns 43
        val result = getDefaultPriorityScoreUseCase(Priority.Later())
        coVerify { getLowestPriorityScoreUseCase() }
        Assertions.assertEquals(42, result)
    }

    @Test
    fun `default "Plan" priority score for should be the lowest score available`() = runBlocking {
        coEvery { getLowestPriorityScoreUseCase() } returns 42
        coEvery { getHighestPriorityScoreUseCase() } returns 84
        val result = getDefaultPriorityScoreUseCase(Priority.Plan("AnyTime"))
        Assertions.assertEquals(42, result)
    }

}

