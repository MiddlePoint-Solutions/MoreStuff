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
    private val scheduleNotificationsAndWorkUseCase = ScheduleNotificationsAndWorkUseCaseImpl(
        scheduler,
        getAppSettingsUseCase,
        updateReviewNotificationScheduleUseCase
    )

    @Test
    fun `should schedule notifications and work and return Either Right on success`() = runBlocking {
        // Arrange
        val appSettings = AppSettings(reviewTime = Pair(10, 30))
        coEvery { getAppSettingsUseCase.invoke() } returns appSettings
        coEvery { updateReviewNotificationScheduleUseCase.invoke(10, 30, false) } returns Either.Right(true)
        coEvery { scheduler.schedulePlannedPriorityUpdate() } returns Unit

        // Act
        val result = scheduleNotificationsAndWorkUseCase.invoke()

        // Assert
        assertEquals(Either.Right(true), result)
        coVerify {
            getAppSettingsUseCase.invoke()
            updateReviewNotificationScheduleUseCase.invoke(10, 30, false)
            scheduler.schedulePlannedPriorityUpdate()
        }
    }

    // Optional: you can add more tests to handle different cases, for example, when scheduling fails.
}


