package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Task

interface CreateTaskMessageUseCase {
    suspend operator fun invoke(task: Task): SimpleResult<Boolean>
}

class CreateTaskMessageUseCaseImpl(
    private val createMessageUseCase: CreateMessageUseCase
) : CreateTaskMessageUseCase {

    override suspend fun invoke(task: Task): SimpleResult<Boolean> {
        createMessageUseCase(task.id, title = task.title, contentType = ContentType.USER_NEW_TASK)
        return Result.Success(true)
    }
}