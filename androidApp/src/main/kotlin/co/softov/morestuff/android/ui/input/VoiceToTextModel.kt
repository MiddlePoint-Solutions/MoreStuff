package co.softov.morestuff.android.ui.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.service.VoiceToTextParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.koin.compose.koinInject
import timber.log.Timber


@Composable
fun voiceToTextModel(
    initialState: VoiceToTextState,
    events: Flow<VoiceToTextUiEvent>,
    voiceToTextParser: VoiceToTextParser,
    appState: AppState = koinInject(),
): VoiceToTextState {
    var state by remember { mutableStateOf(initialState) }


    val currentLanguage = appState.settings.voiceInputLanguage
    LaunchedEffect(currentLanguage) {
        Timber.d("Detected a change in language settings to: $currentLanguage")
        state = state.copy(detectedLanguage = currentLanguage)
        Timber.d("Updated state with new language: ${state.detectedLanguage}")
    }

    LaunchedEffect(Unit) {
        merge(
            events,
            voiceToTextParser.state.map { parserState ->
                when {
                    parserState.isSpeaking && !state.isListening -> VoiceToTextUiEvent.StartListening
                    !parserState.isSpeaking && state.isListening -> VoiceToTextUiEvent.StopListening
                    parserState.spokenText.isNotEmpty() -> VoiceToTextUiEvent.UpdateSpokenText(parserState.spokenText)
                    parserState.error != null -> VoiceToTextUiEvent.ReportError(parserState.error)
                    else -> null
                }
            }.filterNotNull()
        ).collect { event ->
            state = when (event) {
                is VoiceToTextUiEvent.StartListening -> {
                    voiceToTextParser.startListening(state.detectedLanguage)
                    state.copy(isListening = true)
                }
                is VoiceToTextUiEvent.StopListening -> {
                    voiceToTextParser.stopListening()
                    state.copy(isListening = false)
                }
                is VoiceToTextUiEvent.SetDetectedLanguage -> {
                    state.copy(detectedLanguage = event.language)
                }
                is VoiceToTextUiEvent.UpdateSpokenText -> {
                    voiceToTextParser.clearSpokenText()
                    state.copy(spokenText = event.text)
                }
                is VoiceToTextUiEvent.ReportError -> state.copy(error = event.errorMessage)
            }
        }
    }

    return state
}





