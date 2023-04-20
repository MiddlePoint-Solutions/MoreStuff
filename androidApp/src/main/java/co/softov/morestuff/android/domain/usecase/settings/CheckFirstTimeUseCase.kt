package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.repository.UserRepository

interface CheckFirstTimeUseCase {
    suspend operator fun invoke(): Boolean
}

class CheckFirstTimeImplUseCase(
    private val userRepository: UserRepository
) : CheckFirstTimeUseCase {

    override suspend fun invoke(): Boolean {

        return userRepository.isFirstTime()
    }

}