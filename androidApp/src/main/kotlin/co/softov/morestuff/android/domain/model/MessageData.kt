package co.softov.morestuff.android.domain.model

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.enums.MessageDataType
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MessageData(
    val id: Long,
    val filePath: String,
    val creationTime: String,
    val messageType: MessageDataType
): Parcelable