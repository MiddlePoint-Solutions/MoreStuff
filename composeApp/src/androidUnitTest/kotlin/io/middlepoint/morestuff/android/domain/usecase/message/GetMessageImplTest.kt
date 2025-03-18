package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.createMessageForTest
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
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
