package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import arrow.core.right
import co.softov.morestuff.android.domain.createMessage
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetMessageImplTest{
    private val messageRepository: MessageRepository = mockk()
    private val getMessage = GetMessageImpl(messageRepository)

    @Test
    fun `messageRepository getMessage`() {


        val message = createMessage()
        coEvery { messageRepository.getMessage(1L) } returns Either.Right(message)

        runBlocking{
            val result = getMessage.invoke(1L)
            assertEquals(Either.Right(message), result)
        }
    }
}
