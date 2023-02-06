package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.createMessageForTest
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


        val message = createMessageForTest()
        coEvery { messageRepository.getMessage(1L) } returns Either.Right(message)

        runBlocking{
            val result = getMessage.invoke(1L)
            assertEquals(Either.Right(message), result)
        }
    }
}
