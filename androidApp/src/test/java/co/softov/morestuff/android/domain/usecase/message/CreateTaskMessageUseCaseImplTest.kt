package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.createMessage
import co.softov.morestuff.android.domain.createTask
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Task
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CreateTaskMessageUseCaseImplTest{

    private val createMessageUseCase:  CreateMessageUseCase = mockk()
    private val createTaskMessageUseCase = CreateTaskMessageUseCaseImpl(createMessageUseCase)

    @Test
    fun `createMessageUseCase is called with the correct parameters`() = runBlocking   {

        val task = createTask()
        val message = createMessage()
        coEvery { createMessageUseCase(task.id, title = task.title, contentType = ContentType.USER_NEW_TASK) } returns Either.Right(message)

            val result = createTaskMessageUseCase.invoke(task)
            assertTrue(result.isRight())
            assertEquals(true, result.isRight())
        }

    }
