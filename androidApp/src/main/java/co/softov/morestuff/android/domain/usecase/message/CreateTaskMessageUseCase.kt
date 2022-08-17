package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Scope

interface CreateTaskMessageUseCase {
    suspend operator fun invoke(scope: Scope): Either<Failure,Boolean>
}

class CreateTaskMessageUseCaseImpl(
    private val createMessageUseCase: CreateMessageUseCase
) : CreateTaskMessageUseCase {

    override suspend fun invoke(scope: Scope): Either<Failure,Boolean> {
        createMessageUseCase(scope.id, title = scope.title, contentType = ContentType.USER_NEW_TASK)
        return Either.Right(true)
    }
}