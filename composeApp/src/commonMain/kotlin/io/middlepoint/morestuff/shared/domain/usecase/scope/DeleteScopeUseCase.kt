package io.middlepoint.morestuff.shared.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface DeleteScopeUseCase {
    suspend operator fun invoke(scopeId: Uuid): Either<Failure, Scope>
}

class DeleteScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : DeleteScopeUseCase {
    override suspend fun invoke(scopeId: Uuid): Either<Failure, Scope> =
        scopeRepository.deleteScope(scopeId)
}
