package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetUserSettingsUseCaseImplTest {

    private val userRepository = mockk<UserRepository>()
    private val getUserSettingsUseCase = GetAppSettingsUseCaseImpl(userRepository)

    @Test
    fun `should call getUserSettings with correct params`(): Unit = runBlocking {
        val expectedSettings = AppSettings()
        coEvery { userRepository.getAppSettings(expectedSettings) } returns expectedSettings

        getUserSettingsUseCase.invoke()

        coEvery { userRepository.getAppSettings(expectedSettings) }
    }

    @Test
    fun `should return AppSettings `() = runBlocking {
        val expectedSettings = AppSettings()
        coEvery { userRepository.getAppSettings(expectedSettings) } returns expectedSettings

        val result = getUserSettingsUseCase.invoke()

        assertEquals(expectedSettings, result)
    }
}

