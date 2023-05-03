package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.app.features.VoiceToTextParserState
import kotlinx.coroutines.flow.StateFlow

interface VoiceToTextInterface {
    val state: StateFlow<VoiceToTextParserState>
    fun startListening(languageCode: String)
    fun stopListening()
}
