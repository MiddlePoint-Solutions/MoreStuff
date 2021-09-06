package co.softov.morestuff.android.domain.usecase.message

import androidx.paging.PagingSource
import co.softov.morestuff.android.data.mapper.MessageData
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Result
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.android.paging3.QueryPagingSource

interface GetPagedMessages {
    operator fun invoke(): PagedMessagesResult
}

typealias PagedMessagesResult = PagingSource<Long, MessageData>

class GetPagedMessagesImpl(
    private val database: StuffDb
) : GetPagedMessages {
    override fun invoke(): PagedMessagesResult {
        return QueryPagingSource(
            queryProvider = database.messageQueries::pageMessages,
            countQuery = database.messageQueries.countMessages(),
            transacter = database.messageQueries
        )
    }
} 



