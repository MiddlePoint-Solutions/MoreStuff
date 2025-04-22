package io.middlepoint.morestuff.shared.domain.model.core

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ScopeDomain(
  val id: Long,
  val uid: String,
  val name: String,
  val order: Int,
  val scopeType: ScopeType = ScopeType.NORMAL
)

val defaultScope = ScopeDomain(
  id = 1,
  uid = "",
  name = "Stuff",
  order = 0,
)

@Serializable
enum class ScopeType(val value: Int) {
  NORMAL(0),
  IA_SCOPE(1);

  companion object {
    fun withValue(value: Int) = entries.first { it.value == value }
  }
}