package co.softov.morestuff.android.ui.input.voice

import android.os.Parcelable
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.domain.enums.Language
import kotlinx.parcelize.Parcelize
import javax.annotation.concurrent.Immutable

@Parcelize
@Immutable
data class VoiceToTextState(
    val isListening: Boolean = false,
    val detectedLanguage: Language = Language.Device,
    val spokenText: String = "",
    val error: String? = null
) : Parcelable

@Immutable
sealed class VoiceToTextUiEvent : BaseViewEvent {
    data object StartListening : VoiceToTextUiEvent()
    data object StopListening : VoiceToTextUiEvent()
    data class SetDetectedLanguage(val language: Language) : VoiceToTextUiEvent()
    data class UpdateSpokenText(val text: String) : VoiceToTextUiEvent()
    data class ReportError(val errorMessage: String) : VoiceToTextUiEvent()
}
