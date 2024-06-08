package io.middlepoint.morestuff.android.ui.input.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.service.VoiceToTextParser
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.Locale


class VoiceToTextViewModel(
    private val voiceToTextParser: VoiceToTextParser,
    private val savedState: SavedStateHandle,
) : MoleculeViewModel<VoiceToTextUiEvent, VoiceToTextState>() {

    override val initialState: VoiceToTextState = savedState["voice"] ?: VoiceToTextState()

    @Composable
    override fun models(events: Flow<VoiceToTextUiEvent>): VoiceToTextState {
        return voiceToTextModel(
            initialState = initialState,
            events = events,
            voiceToTextParser = voiceToTextParser,
        )
    }
    override fun onSaveState(model: VoiceToTextState) {
        savedState["voice"] = model
        Timber.d("language Saved state: $model")
    }
}

@Composable
fun displayLanguageName(viewModel: VoiceToTextViewModel): String {
    val state by viewModel.models.collectAsState()
    Timber.d("Displaying language: ${state.detectedLanguage}")
    return if (state.detectedLanguage == Language.Device) {
        val defaultLanguage = Locale.getDefault().displayLanguage
        Timber.d("language set to device default: $defaultLanguage")
        defaultLanguage
    } else {
        Timber.d("language explicitly set to: ${state.detectedLanguage.name}")
        state.detectedLanguage.name
    }
}


/*class VoiceToTextViewModel(
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
}*/
