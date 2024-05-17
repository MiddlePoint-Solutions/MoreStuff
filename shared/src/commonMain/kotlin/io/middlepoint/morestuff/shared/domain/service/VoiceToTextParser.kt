package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.app.features.VoiceToTextParserState
import io.middlepoint.morestuff.shared.domain.enums.Language
import kotlinx.coroutines.flow.StateFlow

interface VoiceToTextParser {
    val state: StateFlow<VoiceToTextParserState>
    fun startListening(languageCode: Language)
    fun stopListening()
    fun clearSpokenText()
}
