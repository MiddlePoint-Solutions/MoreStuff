package co.softov.morestuff.android.domain.usecase.settings


import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class SaveUserSettingsUseCaseImplTest {

    private val userRepository = mockk<UserRepository>()
    private val saveUserSettings = SaveUserSettingUseCaseImpl(userRepository)

    // TODO:

}


