package co.softov.morestuff.android.ui.chat.task

import co.softov.morestuff.android.domain.model.Message


data class MessageWithFormattedTime(
    val message: Message,
    val formattedTime: String,
    val formattedTimeOnly: String
)