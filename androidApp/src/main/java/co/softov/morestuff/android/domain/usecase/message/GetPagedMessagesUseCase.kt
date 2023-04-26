package co.softov.morestuff.android.domain.usecase.message

import androidx.paging.PagingSource
import co.softov.morestuff.android.data.mapper.MessageData
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.android.paging3.QueryPagingSource

interface GetPagedMessagesUseCase {
    operator fun invoke(): PagedMessagesResult
}

typealias PagedMessagesResult = PagingSource<Long, MessageData>

class GetPagedMessagesUseCaseImpl(
    private val database: StuffDb
) : GetPagedMessagesUseCase {
    override fun invoke(): PagedMessagesResult {
        return QueryPagingSource(
            queryProvider = database.messageQueries::pageMessages,
            countQuery = database.messageQueries.countMessages(),
            transacter = database.messageQueries
        )
    }
} 



