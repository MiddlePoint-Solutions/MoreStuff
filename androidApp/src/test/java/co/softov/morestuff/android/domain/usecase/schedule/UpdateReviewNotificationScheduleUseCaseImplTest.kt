package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.service.Scheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateReviewNotificationScheduleUseCaseImplTest {

    private val scheduler = mockk<Scheduler>()
    private val updateReviewNotificationScheduleUseCase = UpdateReviewNotificationScheduleUseCaseImpl(scheduler)

    @Test
    fun `update Review Notification Schedule`() = runBlocking {

        val hour = 10
        val minute = 30
        val replaceExisting = true
        coEvery { scheduler.scheduleNextReview(hour, minute, replaceExisting) } returns Unit

        val result = updateReviewNotificationScheduleUseCase(hour, minute, replaceExisting)

        assertEquals(Either.Right(true), result)
        coVerify { scheduler.scheduleNextReview(hour, minute, replaceExisting) }
    }

}
