package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.enums.MessageDataType


data class MessageData(
    val id: Long,
    val filePath: String,
    val creationTime: String,
    val messageType: MessageDataType
)