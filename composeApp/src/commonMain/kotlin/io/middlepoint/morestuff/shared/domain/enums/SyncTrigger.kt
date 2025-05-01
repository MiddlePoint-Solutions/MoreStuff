package io.middlepoint.morestuff.shared.domain.enums

sealed class SyncTrigger {
  data object Auth : SyncTrigger()
  data object Interval : SyncTrigger()
  data object UserInitiated : SyncTrigger()
}