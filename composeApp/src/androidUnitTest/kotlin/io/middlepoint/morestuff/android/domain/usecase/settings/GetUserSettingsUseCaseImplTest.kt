package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.redux.state.AppSettings
import io.middlepoint.morestuff.shared.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
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

        getUserSettingsUseCase()

        coVerify { userRepository.getAppSettings(expectedSettings) }
    }

    @Test
    fun `should return AppSettings `() = runBlocking {
        val expectedSettings = AppSettings()
        coEvery { userRepository.getAppSettings(expectedSettings) } returns expectedSettings

        val result = getUserSettingsUseCase()

        assertEquals(expectedSettings, result)
        coVerify { userRepository.getAppSettings(expectedSettings) }
    }
}


