package io.middlepoint.morestuff.shared.domain.model.core.legacy

import kotlinx.serialization.Serializable

@Serializable
data class LegacyScope(
  val id: Long,
  val uid: String,
  val name: String,
  val order: Int,
)