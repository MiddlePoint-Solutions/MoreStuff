package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.createMessageForTest
import co.softov.morestuff.android.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetTaskChatMessagesImplTest {

    @Test
    fun `invoke should return messages for given taskId`() = runBlocking {

        val messageRepository = mockk<MessageRepository>()
        val taskId = 1L

        val message1 = createMessageForTest(id = 1, taskId = taskId)
        val message2 = createMessageForTest(id = 2, taskId = taskId)

        coEvery { messageRepository.getTaskChatMessagesFlow(taskId) } returns flow {
            emit(listOf(message1, message2))
        }

        val getTaskChatMessagesImpl = GetTaskChatMessagesImpl(messageRepository)

        val messagesFlow = getTaskChatMessagesImpl.invoke(taskId)
        val messages = messagesFlow.toList().flatten()
        assertEquals(2, messages.size)
        assertEquals(message1, messages[0])
        assertEquals(message2, messages[1])
    }
}







