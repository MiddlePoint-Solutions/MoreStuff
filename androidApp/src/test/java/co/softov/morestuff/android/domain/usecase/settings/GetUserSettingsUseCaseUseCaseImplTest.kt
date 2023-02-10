package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Setting
import co.softov.morestuff.android.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetUserSettingsUseCaseUseCaseImplTest {
    private val userRepository = mockk<UserRepository>()
    private val getUserSettingsImpl = GetUserSettingsUseCaseUseCaseImpl(userRepository)
    private val appSettings = AppSettings()
    private val snoozeLimit = Setting.SnoozeLimit(2)
    private val smartReminderEnabled = Setting.SmartReminderEnabled(true)

    @Test
    fun `returns user settings`() = runBlocking {
        coEvery { userRepository.getUserSettings(appSettings) } returns appSettings

        val result = getUserSettingsImpl.invoke()

        assertEquals(appSettings, result)
        coVerify { userRepository.getUserSettings(appSettings) }
    }

    @Test
    fun `returns  modified user settings`() = runBlocking {
        val modifiedAppSettings = AppSettings(snoozeLimit, smartReminderEnabled)
        coEvery { userRepository.getUserSettings(appSettings) } returns modifiedAppSettings

        val result = getUserSettingsImpl.invoke()

        assertEquals(modifiedAppSettings, result)

    }
}

