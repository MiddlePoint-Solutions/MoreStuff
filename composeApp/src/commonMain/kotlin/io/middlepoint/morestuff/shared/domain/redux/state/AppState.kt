package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.enums.SyncStatus
import io.middlepoint.morestuff.shared.domain.enums.isReady
import io.middlepoint.morestuff.shared.domain.model.Uuid

data class AppState(
  val userState: UserState = UserState(),
  val syncState: SyncState = SyncState(),
  val settings: AppSettingsState = AppSettingsState(),
  val aiMessageLoading: Map<Uuid, Boolean> = emptyMap()
)

fun AppState.isReady() =
  userState.status.isReady()
          && settings.status.isReady()

fun AppState.isAuthenticated() = userState.user != null

fun AppState.isSyncInProgress() = syncState.status is SyncStatus.Initializing

