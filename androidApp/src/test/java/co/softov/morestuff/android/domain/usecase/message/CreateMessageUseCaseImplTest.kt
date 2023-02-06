package co.softov.morestuff.android.domain.usecase.message

import arrow.core.right
import co.softov.morestuff.android.domain.createMessageForTest
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class CreateMessageUseCaseImplTest {
    @Test
    fun `should return created message`() {
        val messageRepository: MessageRepository = mockk()
        val createMessageUseCaseImpl = CreateMessageUseCaseImpl(messageRepository)
        val taskId = 1L
        val scheduleId = 1L
        val title = "Test Message"
        val contentType = ContentType.CONFIRM_NEW_TASK
        val expectedMessage = createMessageForTest()

        coEvery { messageRepository.createMessage(taskId, scheduleId, contentType.value, title) } returns expectedMessage.right()

        runBlocking {
            val result = createMessageUseCaseImpl.invoke(taskId, scheduleId, title, contentType)
            assertEquals(expectedMessage.right(), result)
        }
    }
}

