package io.middlepoint.morestuff.android.domain.usecase.message

import io.middlepoint.morestuff.android.domain.createMessageForTest
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GetTaskChatMessagesUseCaseImplTest {

    @Test
    fun `invoke should return messages for given taskId`() = runBlocking {

        val messageRepository = mockk<MessageRepository>()
        val taskId = 1L

        val message1 =
          createMessageForTest(id = 1, taskId = taskId)
        val message2 =
          createMessageForTest(id = 2, taskId = taskId)

        coEvery { messageRepository.getTaskChatMessagesFlow(taskId) } returns flow {
            emit(listOf(message1, message2))
        }

        val getTaskChatMessagesImpl = GetTaskChatMessagesUseCaseImpl(messageRepository)

        val messages = getTaskChatMessagesImpl(taskId).asFlow().toList().flatten()
        assertEquals(listOf(message1, message2), messages)
    }
}







