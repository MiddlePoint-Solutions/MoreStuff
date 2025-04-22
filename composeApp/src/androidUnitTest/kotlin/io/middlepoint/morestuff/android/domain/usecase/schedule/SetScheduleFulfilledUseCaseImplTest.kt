package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import arrow.core.getOrElse
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SetScheduleFulfilledUseCaseImplTest{

    private val scheduleRepository = mockk<ScheduleRepository>()
    private val setScheduleFulfilledUseCaseImpl = SetScheduleFulfilledUseCaseImpl(scheduleRepository)
    private val scheduleId = Uuid("1")

    @Test
    fun `set schedule fulfilled use case impl`() = runBlocking{
        coEvery { scheduleRepository.setScheduleFulfilled(scheduleId) } returns Either.Right(true)
        val result = setScheduleFulfilledUseCaseImpl(scheduleId)
        assertEquals(true, result.getOrNull())
    }

}
