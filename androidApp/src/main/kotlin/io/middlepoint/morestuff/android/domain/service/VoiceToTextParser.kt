package io.middlepoint.morestuff.android.domain.service

import io.middlepoint.morestuff.android.app.features.VoiceToTextParserState
import io.middlepoint.morestuff.android.domain.enums.Language
import kotlinx.coroutines.flow.StateFlow

interface VoiceToTextParser {
    val state: StateFlow<VoiceToTextParserState>
    fun startListening(languageCode: Language)
    fun stopListening()
    fun clearSpokenText()
}
