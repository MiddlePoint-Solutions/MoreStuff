package io.middlepoint.morestuff.shared.domain.model.core.migrate

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MessageDataNew(
    val id: String,
    val filePath: String,
    val creationTime: String,
    val messageType: MessageDataType
)