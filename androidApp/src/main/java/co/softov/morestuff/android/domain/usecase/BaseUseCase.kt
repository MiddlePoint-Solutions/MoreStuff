package co.softov.morestuff.android.domain.usecase

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure

interface BaseUseCase<E : Failure, P, R> {
    suspend operator fun invoke(params: P): Either<E, R>
}