package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.enums.SyncStatus
import io.middlepoint.morestuff.shared.domain.enums.SyncTrigger
import io.middlepoint.morestuff.shared.domain.redux.action.SyncAction
import io.middlepoint.morestuff.shared.domain.redux.action.SyncAction.SyncIntervalAction
import io.middlepoint.morestuff.shared.domain.redux.action.SyncAction.SyncTriggerAction
import io.middlepoint.morestuff.shared.domain.redux.action.SyncAction.UpdateSyncStatusAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import kotlinx.datetime.Instant

data class SyncState(
  val status: SyncStatus? = null,
  val lastSyncTime: Instant? = null,
  val lastSyncTriggers: List<SyncTrigger> = listOf()
)

fun AppState.reduceSyncState(action: Action): AppState {
  return when (action) {
    is SyncAction -> copy(syncState = syncState.reduce(action))
    else -> this
  }
}

fun SyncState.reduce(action: SyncAction): SyncState {
  return when (action) {
    is SyncTriggerAction -> copy(
      lastSyncTriggers = lastSyncTriggers + action.trigger
    )

    is UpdateSyncStatusAction -> copy(
      status = action.status,
      lastSyncTime = (action.status as? SyncStatus.Success)?.syncTime ?: lastSyncTime
    )

    is SyncIntervalAction -> this
  }
}
