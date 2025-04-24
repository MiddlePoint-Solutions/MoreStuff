package io.middlepoint.morestuff.shared.domain.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MessageExtra(
    val id: Uuid,
    val url: String,
    val creationTime: String,
    val messageType: MessageExtraType
)
