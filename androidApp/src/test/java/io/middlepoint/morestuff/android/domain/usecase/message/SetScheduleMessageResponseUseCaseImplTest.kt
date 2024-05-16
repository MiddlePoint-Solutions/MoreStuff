package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.enums.ReplyType
import io.middlepoint.morestuff.android.domain.repository.MessageRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SetScheduleMessageResponseUseCaseImplTest {
    private val messageRepository: MessageRepository = mockk()
    private val setScheduleMessageResponse = SetScheduleMessageResponseUseCaseImpl(messageRepository)

    @Test
    fun `returns success when message repository addUserReplyMessage is successful`() = runBlocking {
        coEvery { messageRepository.addUserReplyMessage(any(), any(), any()) } returns Unit
        val taskId = listOf(1L)
        val title = "title"
        val replyType = ReplyType.DONE
        val result = setScheduleMessageResponse.invoke(taskId, title, replyType)

        coVerify { messageRepository.addUserReplyMessage(any(), replyType.value, title) }
        assertTrue(result.isRight())
        assertEquals(result, Either.Right(true))
    }
}

