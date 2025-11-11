package io.middlepoint.morestuff.shared.data.adapter

import app.cash.sqldelight.ColumnAdapter
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlin.time.Instant

object InstantColumnAdapter : ColumnAdapter<Instant, Long> {
  override fun decode(databaseValue: Long): Instant {
    return Instant.fromEpochMilliseconds(databaseValue)
  }

  override fun encode(value: Instant): Long {
    return value.toEpochMilliseconds()
  }
}

object UuidColumnAdapter : ColumnAdapter<Uuid, String> {

  override fun decode(databaseValue: String): Uuid {
    return Uuid(databaseValue)
  }

  override fun encode(value: Uuid): String {
    return value.value
  }

}