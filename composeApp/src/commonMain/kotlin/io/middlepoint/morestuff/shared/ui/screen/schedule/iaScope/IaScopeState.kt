package io.middlepoint.morestuff.shared.ui.screen.schedule.iaScope

import androidx.compose.runtime.Immutable
import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.ui.model.IAMessageUiModel

@Immutable
data class IAChatState(
    val messages: List<IAMessageUiModel> = listOf(),
    val isAIEnabled: Boolean = true
)


@Immutable
sealed class IAChatEvent {
    data class InputText(val content: String) : IAChatEvent()
    data class InputUserMedia(val imageFile: PlatformFile, val title: String) : IAChatEvent()
    data class InputDocument(val pdfFile: PlatformFile, val title: String) : IAChatEvent()
    data class CopyText(val content: String) : IAChatEvent()
    data class DeleteMessage(val message: IAMessageUiModel) : IAChatEvent()
    data class OpenDocument(val path: String) : IAChatEvent()
    data object ActivateAI : IAChatEvent()
    data class CreateAIMessage(val prompt: String) : IAChatEvent()
}