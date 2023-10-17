package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.redux.state.AppSettings
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.usecase.settings.GetAppSettingsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScheduleNotificationsAndWorkUseCaseImplTest {

    private val scheduler = mockk<Scheduler>()
    private val getAppSettingsUseCase = mockk<GetAppSettingsUseCase>()
    private val updateReviewNotificationScheduleUseCase = mockk<UpdateReviewNotificationScheduleUseCase>()
    private val scheduleNotificationsAndWorkUseCase = ScheduleWorkUseCaseImpl(
        scheduler,
        getAppSettingsUseCase,
        updateReviewNotificationScheduleUseCase
    )

    @Test
    fun `should schedule notifications and work and return Either Right on success`() = runBlocking {
        val appSettings = AppSettings(reviewTime = Pair(10, 30))
        coEvery { getAppSettingsUseCase.invoke() } returns appSettings
        coEvery { updateReviewNotificationScheduleUseCase(10, 30, false) } returns Either.Right(true)
        coEvery { scheduler.schedulePlannedPriorityWorker() } returns Unit

        val result = scheduleNotificationsAndWorkUseCase()

        assertEquals(Either.Right(true), result)
        coVerify {
            getAppSettingsUseCase.invoke()
            updateReviewNotificationScheduleUseCase(10, 30, false)
            scheduler.schedulePlannedPriorityWorker()
        }
    }

}


