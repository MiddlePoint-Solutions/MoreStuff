package io.middlepoint.morestuff.android.domain.usecase.message

import io.middlepoint.morestuff.android.domain.repository.MessageRepository
import io.middlepoint.morestuff.android.domain.createListOfMessages
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetMessagesUseCaseForTaskImplTest {
    private val messageRepository: MessageRepository = mockk()
    private val getMessagesForTask = GetTaskMessagesFlowUseCaseImpl(messageRepository)

    @Test
    fun `messageRepository getMessagesForTask`() {
        val taskId = 1L
        val messages = flowOf(createListOfMessages(2))
        coEvery { messageRepository.getTaskMessagesFlow(taskId) } returns messages

        val result = runBlocking { getMessagesForTask(taskId) }
        assertEquals(messages , result)
    }
}
