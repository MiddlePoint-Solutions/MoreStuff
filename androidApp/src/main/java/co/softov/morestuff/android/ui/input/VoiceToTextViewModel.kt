package co.softov.morestuff.android.ui.input

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.enums.Language
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.service.VoiceToTextParser
import co.softov.morestuff.android.ui.input.VoiceToTextUiEvent.ReportError
import co.softov.morestuff.android.ui.input.VoiceToTextUiEvent.SetDetectedLanguage
import co.softov.morestuff.android.ui.input.VoiceToTextUiEvent.StartListening
import co.softov.morestuff.android.ui.input.VoiceToTextUiEvent.StopListening
import co.softov.morestuff.android.ui.input.VoiceToTextUiEvent.UpdateSpokenText
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale

class VoiceToTextViewModel(
    private val voiceToTextParser: VoiceToTextParser
    ) : BaseViewModel<VoiceToTextUiModel, VoiceToTextUiEvent>(VoiceToTextUiModel()) {

    init {
        loadData()
        setupVoiceToTextParser()
    }

    override fun onAppStateChange(state: AppState) {
        sendEvent(SetDetectedLanguage(state.settings.voiceInputLanguage))
    }

    private fun setupVoiceToTextParser() {
        voiceToTextParser.state
            .onEach { parserState ->
                if (parserState.isSpeaking != state.isListening) {
                    sendEvent(if (parserState.isSpeaking) StartListening else StopListening)
                }
                if (parserState.spokenText.isNotEmpty()) {
                    sendEvent(UpdateSpokenText(parserState.spokenText))
                }
                parserState.error?.let { sendEvent(ReportError(it)) }
            }
            .launchIn(viewModelScope)
    }

    override fun onReduceState(event: VoiceToTextUiEvent): VoiceToTextUiModel {
        return when (event) {
            StartListening -> {
                voiceToTextParser.startListening(state.detectedLanguage)
                state.copy(isListening = true)
            }
            StopListening -> {
                voiceToTextParser.stopListening()
                state.copy(isListening = false)
            }
            is SetDetectedLanguage -> state.copy(detectedLanguage = event.language)
            is UpdateSpokenText -> {
                voiceToTextParser.clearSpokenText()
                state.copy(spokenText = event.text)
            }
            is ReportError -> state.copy(error = event.errorMessage)
        }
    }

    fun startListening() {
        sendEvent(StartListening)
    }

    fun stopListening() {
        sendEvent(StopListening)

    }
    fun displayLanguageName(): String {
        return if (state.detectedLanguage == Language.Device) {
            Locale.getDefault().displayLanguage
        } else {
            state.detectedLanguage.name
        }
    }

    fun clearInput() {
        sendEvent(UpdateSpokenText(""))
    }
}
