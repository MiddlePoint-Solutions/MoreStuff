package io.middlepoint.morestuff.shared.ui.components.input.voice

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.Language
import kotlinx.serialization.Serializable

@Immutable
data class VoiceToTextState(
  val isListening: Boolean = false,
  val detectedLanguage: Language = Language.Device,
  val spokenText: String = "",
  val error: String? = null
)

@Immutable
sealed class VoiceToTextUiEvent {
  data object StartListening : VoiceToTextUiEvent()
  data object StopListening : VoiceToTextUiEvent()
  data class SetDetectedLanguage(val language: Language) : VoiceToTextUiEvent()
  data class UpdateSpokenText(val text: String) : VoiceToTextUiEvent()
  data class ReportError(val errorMessage: String) : VoiceToTextUiEvent()
}
