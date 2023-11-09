package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.app.features.VoiceToTextParserState
import co.softov.morestuff.android.domain.enums.Language
import kotlinx.coroutines.flow.StateFlow

interface VoiceToTextParser {
    val state: StateFlow<VoiceToTextParserState>
    fun startListening(languageCode: Language)
    fun stopListening()
}
