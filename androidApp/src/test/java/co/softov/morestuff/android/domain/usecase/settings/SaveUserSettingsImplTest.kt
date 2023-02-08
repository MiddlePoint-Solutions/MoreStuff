package co.softov.morestuff.android.domain.usecase.settings


import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Setting
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test


class SaveUserSettingsImplTest {
    private val appSettings = mockk<AppSettings>(relaxed = true)
    private val saveUserSettingsImpl = SaveUserSettingsImpl(appSettings.copy())

    @Test
    fun `saves user settings`() = runBlocking {
        val snoozeLimit = Setting.SnoozeLimit(3)
        val settings = AppSettings(snoozeLimit = snoozeLimit)

        saveUserSettingsImpl.invoke(settings)


    }
}


