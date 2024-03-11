package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.service.Scheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class UpdateReviewNotificationScheduleUseCaseImplTest {

    private val scheduler = mockk<Scheduler>()
    private val useCase = UpdateReviewNotificationScheduleUseCaseImpl(scheduler)

    @Test
    fun `update Review Notification Schedule`() = runBlocking {
        val hour = 10
        val minute = 30
        coEvery { scheduler.scheduleReviewWorker(hour, minute) } returns Unit
        useCase(hour, minute)
        coVerify { scheduler.scheduleReviewWorker(hour, minute) }
    }

}
