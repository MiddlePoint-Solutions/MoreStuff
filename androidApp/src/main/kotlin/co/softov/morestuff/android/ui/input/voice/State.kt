package co.softov.morestuff.android.ui.input.voice

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.enums.Language
import javax.annotation.concurrent.Immutable

@Immutable
data class VoiceToTextUiModel(
    val isListening: Boolean = false,
    val detectedLanguage: Language = Language.Device,
    val spokenText: String = "",
    val error: String? = null
) : BaseViewState

@Immutable
sealed class VoiceToTextUiEvent : BaseViewEvent {
    data object StartListening : VoiceToTextUiEvent()
    data object StopListening : VoiceToTextUiEvent()
    data class SetDetectedLanguage(val language: Language) : VoiceToTextUiEvent()
    data class UpdateSpokenText(val text: String) : VoiceToTextUiEvent()
    data class ReportError(val errorMessage: String) : VoiceToTextUiEvent()
}
