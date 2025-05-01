package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.enums.SyncStatus
import io.middlepoint.morestuff.shared.domain.enums.SyncTrigger
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class SyncAction : Action.FeatureAction() {
  data class UpdateSyncStatusAction(val status: SyncStatus) : SyncAction()
  data class SyncTriggerAction(val trigger: SyncTrigger) : SyncAction()
  data object SyncIntervalAction : SyncAction()
}