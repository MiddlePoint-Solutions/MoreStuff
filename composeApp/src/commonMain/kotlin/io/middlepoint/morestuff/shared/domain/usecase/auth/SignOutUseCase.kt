package io.middlepoint.morestuff.shared.domain.usecase.auth

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.AuthRepository

interface SignOutUseCase {
  suspend operator fun invoke(): Either<Failure, Boolean>
}

class SignOutUseCaseImpl(
  private val authRepository: AuthRepository
) : SignOutUseCase {

  override suspend fun invoke(): Either<Failure, Boolean> = authRepository.signOut()

}
