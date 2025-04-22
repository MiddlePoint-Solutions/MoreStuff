package io.middlepoint.morestuff.android.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.android.domain.createListOfMessages
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskMessagesFlowUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetMessagesUseCaseForTaskNewImplTest {
    private val messageRepository: MessageRepository = mockk()
    private val getMessagesForTask = GetTaskMessagesFlowUseCaseImpl(messageRepository)

    @Test
    fun `messageRepository getMessagesForTask`() {
        val taskId = Uuid("1")
        val messages = flowOf(createListOfMessages(2))
        coEvery { messageRepository.getTaskMessagesFlow(taskId) } returns messages

        val result = runBlocking { getMessagesForTask.invoke(taskId) }
        assertEquals(messages , result)
    }
}
