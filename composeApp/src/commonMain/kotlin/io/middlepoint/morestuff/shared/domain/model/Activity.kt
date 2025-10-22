package io.middlepoint.morestuff.shared.domain.model

import io.middlepoint.morestuff.shared.domain.enums.ActivityType
import kotlin.time.Instant

data class Activity(
  val id: Uuid,
  val sentence: String,
  val createdAt: Instant,
  val data: ActivityType
)
