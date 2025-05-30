package io.middlepoint.morestuff.shared.data.utils

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.platform.generateUUID

fun Uuid.Companion.generate() = Uuid(generateUUID())