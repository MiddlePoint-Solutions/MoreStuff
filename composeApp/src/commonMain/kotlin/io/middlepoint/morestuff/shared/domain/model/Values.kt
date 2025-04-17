package io.middlepoint.morestuff.shared.domain.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

// For JVM backends
@JvmInline
@Serializable
value class Uuid(val value: String) {
  companion object
}