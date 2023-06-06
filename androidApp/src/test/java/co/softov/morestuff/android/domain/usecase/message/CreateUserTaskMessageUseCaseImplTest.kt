package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.createMessageForTest
import co.softov.morestuff.android.domain.createTaskForTest
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.ui.chat.items.OpenGraphResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CreateUserTaskMessageUseCaseImplTest {

    private val createMessageUseCase: CreateMessageUseCase = mockk()
    private val fetchOpenGraphMetadataUseCase: FetchOpenGraphMetadataUseCase = mockk()
    private val messageRepository: MessageRepository = mockk()
    private val createTaskMessageUseCase = CreateUserTaskMessageUseCaseImpl(
        createMessageUseCase,
        fetchOpenGraphMetadataUseCase,
        messageRepository
    )
    private val openGraphResult = mockk<OpenGraphResult>()


    @Test
    fun `createMessageUseCase is called with the correct parameters`() = runBlocking {

        val task = createTaskForTest()
        val message = createMessageForTest()
        val url = "http://test.com"
        val taskWithUrl = task.copy(title = "Test title $url")

        val openGraphResult: OpenGraphResult = openGraphResult
        coEvery { createMessageUseCase(taskWithUrl.id, title = taskWithUrl.title, contentType = ContentType.TASK_MESSAGE) } returns Either.Right(message)
        coEvery { fetchOpenGraphMetadataUseCase.invoke(url, message.id) } returns openGraphResult
        coEvery { messageRepository.insertUrlMetadata(url, openGraphResult, message.id) } returns Unit

        val result = createTaskMessageUseCase.invoke(taskWithUrl)

        coVerify { createMessageUseCase(taskWithUrl.id, title = taskWithUrl.title, contentType = ContentType.TASK_MESSAGE) }
        coVerify { fetchOpenGraphMetadataUseCase.invoke(url, message.id) }
        coVerify { messageRepository.insertUrlMetadata(url, openGraphResult, message.id) }

        assertTrue(result.isRight())
        assertEquals(true, result.isRight())
    }
}
