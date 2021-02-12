package co.softov.morestuff.android.domain.usecase.message

import androidx.paging.DataSource
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Result
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory

interface GetPagedMessages {
    suspend operator fun invoke(): SimpleResult<PagedMessagesResult>
}

typealias PagedMessagesResult = DataSource.Factory<Int, Message>

class GetPagedMessagesImpl(
    private val database: StuffDb,
    private val mapMessageDb: MessageDbMapper
) : GetPagedMessages {
    override suspend fun invoke(): SimpleResult<PagedMessagesResult> {
        val result = QueryDataSourceFactory(
            queryProvider = database.messageQueries::pageMessages,
            countQuery = database.messageQueries.countMessages(),
            transacter = database.messageQueries
        ).map { mapMessageDb(it) }
        return Result.Success(result)
    }
} 



