package io.middlepoint.morestuff.android.ui.input.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.android.ui.input.voice.VoiceToTextUiEvent.*
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.service.VoiceToTextParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
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
    store: AppStore = koinInject(),
): VoiceToTextState {
    var state by remember { mutableStateOf(initialState) }

    LaunchedEffect(Unit) {
        store.state.collectLatest { appState ->
            state = state.copy(detectedLanguage = appState.settings.voiceInputLanguage)
        }
    }

    LaunchedEffect(Unit) {
        merge(
            events,
            voiceToTextParser.state.map { parserState ->
                // TODO: is this really needed?
                when {
                    parserState.isSpeaking && !state.isListening -> StartListening
                    !parserState.isSpeaking && state.isListening -> StopListening
                    parserState.spokenText.isNotEmpty() -> UpdateSpokenText(parserState.spokenText)
                    parserState.error != null -> ReportError(parserState.error!!)
                    else -> null
                }
            }.filterNotNull()
        ).collect { event ->
            state = when (event) {
                is StartListening -> {
                    voiceToTextParser.startListening(state.detectedLanguage)
                    state.copy(isListening = true)
                }

                is StopListening -> {
                    voiceToTextParser.stopListening()
                    state.copy(isListening = false)
                }

                is SetDetectedLanguage -> {
                    state.copy(detectedLanguage = event.language)
                }

                is UpdateSpokenText -> {
                    voiceToTextParser.clearSpokenText()
                    state.copy(spokenText = event.text)
                }

                is ReportError -> state.copy(error = event.errorMessage)
            }
        }
    }

    return state
}





