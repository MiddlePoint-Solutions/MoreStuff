package io.middlepoint.morestuff.shared.domain.enums

import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import kotlin.time.Instant

sealed class SyncStatus {
  data object Initializing : SyncStatus()
  data class Success(val syncTime: Instant) : SyncStatus()
  data class Error(val failure: SyncFailure) : SyncStatus()
}

data class SyncFailure(val reason: String) : FeatureFailure