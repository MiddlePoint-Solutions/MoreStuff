package co.softov.morestuff.android.ui.input

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.service.VoiceToTextParser
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class VoiceToTextViewModel(
    private val voiceToTextParser: VoiceToTextParser
) : BaseViewModel<VoiceToTextUiModel, VoiceToTextUiEvent>(VoiceToTextUiModel()) {

    init {
        setupVoiceToTextParser()
    }
    private fun setupVoiceToTextParser() {
        voiceToTextParser.state
            .onEach { parserState ->
                if (parserState.isSpeaking != state.isListening) {
                    sendEvent(if (parserState.isSpeaking) VoiceToTextUiEvent.StartListening else VoiceToTextUiEvent.StopListening)
                }
                if (parserState.spokenText.isNotEmpty()) {
                    sendEvent(VoiceToTextUiEvent.UpdateSpokenText(parserState.spokenText))
                }
                parserState.error?.let { sendEvent(VoiceToTextUiEvent.ReportError(it)) }
            }
            .launchIn(viewModelScope)
    }

    override fun onReduceState(event: VoiceToTextUiEvent): VoiceToTextUiModel {
        return when (event) {
            VoiceToTextUiEvent.StartListening -> state.copy(isListening = true)
            VoiceToTextUiEvent.StopListening -> state.copy(isListening = false)
            is VoiceToTextUiEvent.SetDetectedLanguage -> state.copy(detectedLanguage = event.language)
            is VoiceToTextUiEvent.UpdateSpokenText -> state.copy(spokenText = event.text)
            is VoiceToTextUiEvent.ReportError -> state.copy(error = event.errorMessage)
        }
    }

    fun startListening() {
        val language = state.detectedLanguage
        sendEvent(VoiceToTextUiEvent.SetDetectedLanguage(language))
        voiceToTextParser.startListening(language)
    }

    fun stopListening() {
        sendEvent(VoiceToTextUiEvent.StopListening)
        voiceToTextParser.stopListening()
    }
}
