package io.middlepoint.morestuff.shared.domain.service

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.SyncStatus
import io.middlepoint.morestuff.shared.domain.model.Failure
import kotlinx.coroutines.flow.Flow

interface DataSyncManager {
  fun sync(): Flow<SyncStatus>
  suspend fun push()
  suspend fun pull()
  suspend fun clearAll()
}
