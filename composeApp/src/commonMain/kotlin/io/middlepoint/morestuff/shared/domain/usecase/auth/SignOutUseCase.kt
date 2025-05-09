package io.middlepoint.morestuff.shared.domain.usecase.auth

import arrow.core.Either
import arrow.core.raise.either
import io.middlepoint.morestuff.shared.domain.model.AuthFailure
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.AuthRepository
import io.middlepoint.morestuff.shared.domain.service.DataSyncManager

interface SignOutUseCase {
  suspend operator fun invoke(): Either<Failure, Boolean>
}

class SignOutUseCaseImpl(
  private val syncManager: DataSyncManager,
  private val authRepository: AuthRepository
) : SignOutUseCase {
  override suspend fun invoke(): Either<Failure, Boolean> = either {
    try {
      syncManager.push()
      syncManager.clearAll()
      authRepository.signOut().bind()
    } catch (e: Exception) {
      raise(AuthFailure(e.message))
    }
  }
}
