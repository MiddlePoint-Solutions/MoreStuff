package io.middlepoint.morestuff.shared.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface GetScopeByTaskIdUseCase {
    suspend operator fun invoke(id: Uuid): Either<Failure, Scope>
}

class GetScopeByTaskIdUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetScopeByTaskIdUseCase {
    override suspend fun invoke(id: Uuid): Either<Failure, Scope> {
        return scopeRepository.getScopeByTaskId(id)
    }
}