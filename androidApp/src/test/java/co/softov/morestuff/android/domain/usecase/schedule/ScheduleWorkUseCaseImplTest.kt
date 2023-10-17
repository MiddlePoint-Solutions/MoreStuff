package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.service.Scheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScheduleWorkUseCaseImplTest {

    private val scheduler = mockk<Scheduler>(relaxed = true)
    private val scheduleWorkUseCase = ScheduleWorkUseCaseImpl(scheduler)

    @Test
    fun `should schedule notifications and work and return Either Right on success`() =
        runBlocking {
            val result = scheduleWorkUseCase()
            assertEquals(Either.Right(true), result)
            coVerify {
                scheduler.schedulePlannedPriorityWorker()
                scheduler.scheduleReviewWorker()
            }
        }

}


