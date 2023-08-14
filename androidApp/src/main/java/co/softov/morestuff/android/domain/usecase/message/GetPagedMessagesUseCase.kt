package co.softov.morestuff.android.domain.usecase.message

import androidx.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import co.softov.morestuff.android.data.mapper.MessageDb
import co.softov.morestuff.db.StuffDb
import kotlinx.coroutines.Dispatchers

interface GetPagedMessagesUseCase {
    operator fun invoke(): PagedMessagesResult
}

typealias PagedMessagesResult = PagingSource<Int, MessageDb>

class GetPagedMessagesUseCaseImpl(
    private val database: StuffDb
) : GetPagedMessagesUseCase {
    override fun invoke(): PagedMessagesResult {
        return QueryPagingSource(
            countQuery = database.messageQueries.countMessages(),
            transacter = database.messageQueries,
            context = Dispatchers.IO,
            queryProvider = database.messageQueries::pageMessages
        )
    }
} 



