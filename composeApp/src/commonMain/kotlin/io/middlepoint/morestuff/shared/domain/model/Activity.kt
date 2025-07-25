package io.middlepoint.morestuff.shared.domain.model

import io.middlepoint.morestuff.shared.data.utils.generate
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Activity(
  val id: Uuid = Uuid.generate(),
  val sentence: String,
  val createdAt: Instant = Clock.System.now(),
  val data: String
)
