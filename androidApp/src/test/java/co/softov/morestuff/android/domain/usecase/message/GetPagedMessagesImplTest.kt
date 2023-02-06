package co.softov.morestuff.android.domain.usecase.message

import androidx.paging.PagingSource
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.db.MessageQueries
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.Query
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/*
class GetPagedMessagesImplTest {
    private val database: StuffDb = mockk()
    private val messageQueries: MessageQueries = mockk()
    private val getPagedMessages = GetPagedMessagesImpl(database)

    @Test
    fun `returns paged messages`() = runBlocking {
        coEvery { database.messageQueries } returns messageQueries
        val messages = listOf(
            Message(1, 1, 1, ContentType.CONFIRM_NEW_TASK, "", "", "", ReplyType.DONE, "", ""),
            Message(2, 2, 2, ContentType.USER_NEW_TASK, "", "", "", ReplyType.DONE, "", "")
        )

        coEvery { messageQueries.pageMessages(any(), any()) } returns PageSource(messages.map { it.toDbMessage() })
        coEvery { messageQueries.countMessages() } returns mockk {
            every { executeAsOne() } returns 10}

        val result = getPagedMessages.invoke()

        coVerify { messageQueries.countMessages() }
        coVerify { messageQueries.pageMessages(any(), any()) }
        assertEquals(messages, result)
    }
}*/


/*class GetPagedMessagesImplTest {
    private val database: StuffDb = mockk()
    private val messageQueries: MessageQueries = mockk()
    private val getPagedMessages = GetPagedMessagesImpl(database)

    @Test
    fun `returns paged messages`() = runBlocking {
        coEvery { database.messageQueries } returns messageQueries
        val messages = listOf(
            Message(1, 1, 1, ContentType.CONFIRM_NEW_TASK, "", "", "", ReplyType.DONE, "", ""),
            Message(2, 2, 2, ContentType.USER_NEW_TASK, "", "", "", ReplyType.DONE, "", "")
        )
        val pagingSource = mockk<PagingSource<Int, String>>()
        every { database.messageQueries.pageMessages(limit = any(), offset = any()) } returns pagingSource

        coEvery { messageQueries.countMessages() } returns mockk {
            every { executeAsOne() } returns messages.size.toLong()
        }

        val result = getPagedMessages.invoke()

        coVerify { messageQueries.countMessages() }
        coVerify { messageQueries.pageMessages(any(), any(), any()) }
        assertEquals(messages, result)
    }
}*/




/*class GetPagedMessagesImplTest {
    private val database = mockk<StuffDb>()
    private val messageQueries: MessageQueries = mockk()

    @Test
    fun testInvoke() = runBlocking {
        val pagingSource = mockk<PagingSource<Int, String>>()
        val query = mockk<Query<co.softov.morestuff.db.Message>>()

        every { database.messageQueries.pageMessages(any(), any()) } returns query

        val pagedMessagesImpl = GetPagedMessagesImpl(database)
        val result = pagedMessagesImpl.invoke()

        assertTrue(true)
        assertTrue(result === pagingSource)
    }
}*/

