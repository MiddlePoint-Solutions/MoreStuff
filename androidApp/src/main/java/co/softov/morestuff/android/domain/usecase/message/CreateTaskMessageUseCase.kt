package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.TaskDomain

interface CreateTaskMessageUseCase {
    suspend operator fun invoke(task: TaskDomain): Either<Failure,Boolean>
}

class CreateTaskMessageUseCaseImpl(
    private val createMessageUseCase: CreateMessageUseCase
) : CreateTaskMessageUseCase {

    override suspend fun invoke(task: TaskDomain): Either<Failure,Boolean> {
        createMessageUseCase(task.id, title = task.title, contentType = ContentType.USER_NEW_TASK)
        return Either.Right(true)
    }
}