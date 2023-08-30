package co.softov.morestuff.android.ui.chat.task

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.model.Message

@Immutable
data class MessageUiModel(
    val message: Message,
    val formattedTime: String,
    val formattedTimeOnly: String
)