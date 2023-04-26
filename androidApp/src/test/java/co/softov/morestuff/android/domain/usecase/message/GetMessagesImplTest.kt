package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.createListOfMessages
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetMessagesImplTest{
     private val messageRepository: MessageRepository = mockk()
     private val getMessages = GetMessagesImpl(messageRepository)

     @Test
     fun `messageRepository getAllMessages`() {

         val messages = createListOfMessages(2)
         coEvery { messageRepository.getAllMessages() } returns flowOf(messages)

         runBlocking{
             val result = getMessages.invoke().first()
             assertEquals(messages, result)
         }

     }



 }