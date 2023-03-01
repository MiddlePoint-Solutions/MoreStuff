package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.createListOfMessages
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetMessagesForTaskImplTest {
    private val messageRepository: MessageRepository = mockk()
    private val getMessagesForTask = GetMessagesForTaskImpl(messageRepository)

    @Test
    fun `messageRepository getMessagesForTask`() {
        val taskId = 1L
        val messages = flowOf(createListOfMessages(2))
        coEvery { messageRepository.getMessagesForTask(taskId) } returns messages

        val result = runBlocking { getMessagesForTask(taskId) }
        assertEquals(messages , result)
    }
}
