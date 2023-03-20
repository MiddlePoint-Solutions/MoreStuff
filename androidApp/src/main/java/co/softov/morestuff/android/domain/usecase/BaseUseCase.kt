package co.softov.morestuff.android.domain.usecase

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import kotlinx.coroutines.flow.Flow

interface BaseUseCase<E : Failure, P, R> {
    suspend operator fun invoke(params: P): Either<E, R>
}

interface BaseFlowUseCase<E : Failure, P, R> {
    operator fun invoke(params: P): Flow<Either<E, R>>
}