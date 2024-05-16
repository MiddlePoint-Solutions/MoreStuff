package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.right
import io.middlepoint.morestuff.android.domain.createMessageForTest
import io.middlepoint.morestuff.android.domain.enums.ContentType
import io.middlepoint.morestuff.android.domain.model.MessageData
import io.middlepoint.morestuff.android.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class CreateMessageUseCaseImplTest {
    @Test
    fun `should return created message`() {
        val messageRepository: MessageRepository = mockk()
        val checkForUrlMetadataUseCase: CheckForUrlMetadataUseCase = mockk()
        val createMessageUseCaseImpl = CreateMessageUseCaseImpl(messageRepository,checkForUrlMetadataUseCase )
        val taskId = 1L
        val scheduleId = 1L
        val title = "Test Message"
        val contentType = ContentType.CONFIRM_NEW_TASK
        val messageData: MessageData? = null
        val expectedMessage = createMessageForTest()

        coEvery { messageRepository.createMessage(taskId, scheduleId, contentType.value,messageData, title) } returns expectedMessage.right()
        coEvery { checkForUrlMetadataUseCase(title, any()) } returns Unit.right()

        runBlocking {
            val result = createMessageUseCaseImpl.invoke(taskId,title,   contentType,messageData, scheduleId)
            assertEquals(expectedMessage.right(), result)
        }
    }
}

