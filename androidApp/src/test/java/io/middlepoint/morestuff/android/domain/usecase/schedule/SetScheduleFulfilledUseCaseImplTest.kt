package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SetScheduleFulfilledUseCaseImplTest{

    private val scheduleRepository = mockk<ScheduleRepository>()
    private val setScheduleFulfilledUseCaseImpl = SetScheduleFulfilledUseCaseImpl(scheduleRepository)
    private val scheduleId = 1L

    @Test
    fun `set schedule fulfilled use case impl`() = runBlocking{
        coEvery { scheduleRepository.setScheduleFulfilled(scheduleId) } returns Either.Right(1L)
        val result = setScheduleFulfilledUseCaseImpl.invoke(scheduleId)
        assertTrue(result is Either.Right)
        assertEquals(true, result.isRight())
    }

}
