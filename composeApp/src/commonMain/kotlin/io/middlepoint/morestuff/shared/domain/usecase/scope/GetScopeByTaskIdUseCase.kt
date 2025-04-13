package io.middlepoint.morestuff.shared.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface GetScopeByTaskIdUseCase {
    suspend operator fun invoke(id: Long): Either<Failure, Scope>
}

class GetScopeByTaskIdUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetScopeByTaskIdUseCase {
    override suspend fun invoke(id: Long): Either<Failure, Scope> {
        return scopeRepository.getScopeByTaskId(id)
    }
}