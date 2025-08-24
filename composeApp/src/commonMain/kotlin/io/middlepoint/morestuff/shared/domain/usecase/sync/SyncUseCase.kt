package io.middlepoint.morestuff.shared.domain.usecase.sync

import io.middlepoint.morestuff.shared.domain.enums.SyncStatus
import io.middlepoint.morestuff.shared.domain.service.DataSyncManager
import kotlinx.coroutines.flow.Flow

interface SyncUseCase {
  operator fun invoke(): Flow<SyncStatus>
}

class SyncUseCaseImpl(
  private val syncManager: DataSyncManager
) : SyncUseCase {
  override fun invoke(): Flow<SyncStatus> = syncManager.sync()
}