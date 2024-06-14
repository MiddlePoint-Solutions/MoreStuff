package io.middlepoint.morestuff.shared.domain.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MessageData(
    val id: Long,
    val filePath: String,
    val creationTime: String,
    val messageType: MessageDataType
)