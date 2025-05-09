package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.enums.SyncStatus
import kotlinx.coroutines.flow.Flow

interface DataSyncManager {
  fun sync(): Flow<SyncStatus>
  suspend fun push()
  suspend fun pull()
  suspend fun resetData()
}
