package io.middlepoint.morestuff.shared.domain.model.core

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MessageExtra(
    val id: Uuid,
    val url: String,
    val creationTime: String,
    val messageType: MessageExtraType
)
