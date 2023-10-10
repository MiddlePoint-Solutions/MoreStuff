package co.softov.morestuff.android.domain.usecase.settings

import arrow.core.Either
import co.softov.morestuff.android.domain.redux.state.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import co.softov.morestuff.android.domain.usecase.schedule.UpdateReviewNotificationScheduleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetUserSettingsUseCaseImplTest {

    private val userRepository = mockk<UserRepository>()
    private val updateReviewNotificationScheduleUseCase = mockk<UpdateReviewNotificationScheduleUseCase>()
    private val getAppSettingsUseCase = GetAppSettingsUseCaseImpl(userRepository, updateReviewNotificationScheduleUseCase)

    @Test
    fun `should call getAppSettings with correct params`(): Unit = runBlocking {
        val expectedSettings = AppSettings()
        coEvery { userRepository.getAppSettings(expectedSettings) } returns expectedSettings
        coEvery { updateReviewNotificationScheduleUseCase.invoke(any(), any(), any()) } returns Either.Right(true)

        getAppSettingsUseCase.invoke()

        coVerify { userRepository.getAppSettings(expectedSettings) }
    }

    @Test
    fun `should return AppSettings `() = runBlocking {
        val expectedSettings = AppSettings()
        coEvery { userRepository.getAppSettings(expectedSettings) } returns expectedSettings
        coEvery { updateReviewNotificationScheduleUseCase.invoke(any(), any(), any()) } returns Either.Right(true)

        val result = getAppSettingsUseCase.invoke()

        assertEquals(expectedSettings, result)
    }
}


