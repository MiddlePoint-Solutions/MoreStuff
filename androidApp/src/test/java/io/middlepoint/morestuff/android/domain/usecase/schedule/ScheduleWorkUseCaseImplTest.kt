package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.service.Scheduler
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScheduleWorkUseCaseImplTest {

    private val scheduler = mockk<Scheduler>(relaxed = true)
    private val scheduleReviewUseCase = UpdateReviewNotificationScheduleUseCaseImpl(scheduler)
    private val scheduleWorkUseCase = ScheduleWorkUseCaseImpl(scheduler, scheduleReviewUseCase)

    @Test
    fun `should schedule notifications and work and return Either Right on success`() =
        runBlocking {
            scheduleWorkUseCase(0 to 0)
            coVerify {
                scheduler.schedulePlannedPriorityWorker()
                scheduler.scheduleReviewWorker(0, 0)
            }
        }

}


