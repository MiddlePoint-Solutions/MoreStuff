package io.middlepoint.morestuff.shared.domain.model.core

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Scope(
  val id: Uuid,
  val name: String,
  val order: Int,
  val createdAt: Instant,
  val updatedAt: Instant,
)

const val DEFAULT_SCOPE_NAME = "Stuff"
