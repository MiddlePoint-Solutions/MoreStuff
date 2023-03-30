package co.softov.morestuff.android.domain.usecase.settings


import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class SaveUserSettingsImplTest {

    private val userRepository = mockk<UserRepository>()
    private val saveUserSettings = SaveUserSettingsImpl(userRepository)

    @Test
    fun `should call setSnoozeLimit with correct param`() = runBlocking {
        val expectedSnoozeLimit = 10
        val expectedSettings = AppSettings(snoozeLimit = expectedSnoozeLimit)
        coEvery { userRepository.setSnoozeLimit(expectedSnoozeLimit) } returns Unit
        saveUserSettings(expectedSettings)

        coVerify { userRepository.setSnoozeLimit(expectedSnoozeLimit) }
    }

    @Test
    fun `should return AppSettings `() = runBlocking {
        val expectedSnoozeLimit = 5
        val expectedSettings = AppSettings(snoozeLimit = expectedSnoozeLimit)
        coEvery { userRepository.setSnoozeLimit(expectedSnoozeLimit) } returns Unit
        val result = saveUserSettings(expectedSettings)

        assertEquals(expectedSettings, result)
    }
}


