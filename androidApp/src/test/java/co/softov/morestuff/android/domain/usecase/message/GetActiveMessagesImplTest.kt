package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.createListOfMessages
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetActiveMessagesImplTest{

     private val messageRepository: MessageRepository = mockk()
     private val getActiveMessages = GetActiveMessagesImpl(messageRepository)

     @Test
     fun `getActiveMessages getActiveReminderMessages on the messageRepository`() {
         val expectedMessages = createListOfMessages(2)
         coEvery { messageRepository.getActiveReminderMessages() } returns expectedMessages
         runBlocking{
             val result = getActiveMessages.invoke()
             assertEquals(expectedMessages, result)
         }

     }




 }