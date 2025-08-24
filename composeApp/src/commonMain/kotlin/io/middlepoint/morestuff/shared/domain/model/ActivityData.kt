package io.middlepoint.morestuff.shared.domain.model

import io.middlepoint.morestuff.shared.domain.enums.ActivityType
import kotlinx.serialization.Serializable

@Serializable
data class ActivityData(
  val activityType: ActivityType,
  val taskIds: List<Uuid>
)

