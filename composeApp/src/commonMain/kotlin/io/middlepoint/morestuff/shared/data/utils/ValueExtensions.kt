package io.middlepoint.morestuff.shared.data.utils

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.generateUUID

inline fun Uuid.Companion.generate() = Uuid(generateUUID())