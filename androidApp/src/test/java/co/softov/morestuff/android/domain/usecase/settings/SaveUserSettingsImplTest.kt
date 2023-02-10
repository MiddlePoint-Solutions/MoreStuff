package co.softov.morestuff.android.domain.usecase.settings


import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Setting
import co.softov.morestuff.android.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test




class SaveUserSettingsImplTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val saveUserSettingsImpl = SaveUserSettingsImpl(userRepository)

    @Test
    fun `saves user settings`() = runBlocking {
        val snoozeLimit = Setting.SnoozeLimit(3)
        val settings = AppSettings(snoozeLimit = snoozeLimit)

        saveUserSettingsImpl.invoke(settings)

        coVerify { userRepository.setSnoozeLimit(snoozeLimit.value) }
    }
}


