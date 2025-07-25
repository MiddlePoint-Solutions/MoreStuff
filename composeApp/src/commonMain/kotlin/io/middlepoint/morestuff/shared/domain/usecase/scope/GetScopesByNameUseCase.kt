package io.middlepoint.morestuff.shared.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface GetScopesByNameUseCase {
  suspend operator fun invoke(name: String): Either<Failure, List<Scope>>
}

class GetScopesByNameUseCaseImpl(
  private val scopeRepository: ScopeRepository
) : GetScopesByNameUseCase {
  override suspend fun invoke(name: String): Either<Failure, List<Scope>> {
    return scopeRepository.getScopesByName(name)
  }
}