package io.middlepoint.morestuff.android.domain

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope

val DEFAULT_SCOPE = Scope(
  id = Uuid("1"),
  name = "Stuff",
  order = 0,
  createdAt = timeManager.nowUtcInstant,
  updatedAt = timeManager.nowUtcInstant,
  deleted = false
)