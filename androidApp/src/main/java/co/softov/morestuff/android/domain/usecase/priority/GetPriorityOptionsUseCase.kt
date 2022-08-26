package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.PriorityOption

interface BaseUseCase<E : Failure, P, R> {
    suspend operator fun invoke(params: P): Either<E, R>
}

data class GetPriorityOptionsParams(val priority: Priority)

data class PriorityOptionsResult(
    val priority: Priority,
    val options: List<PriorityOption>,
)

class GetPriorityOptionsUseCase :
    BaseUseCase<Failure, GetPriorityOptionsParams, PriorityOptionsResult> {

    override suspend fun invoke(params: GetPriorityOptionsParams): Either<Failure, PriorityOptionsResult> {
        TODO("Not yet implemented")
    }

}