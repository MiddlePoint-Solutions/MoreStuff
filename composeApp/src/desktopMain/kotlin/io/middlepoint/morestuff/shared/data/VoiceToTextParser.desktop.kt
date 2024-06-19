package io.middlepoint.morestuff.shared.data

import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.model.VoiceToTextParserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceToTextParserImpl : VoiceToTextParser {

    private val _state = MutableStateFlow(VoiceToTextParserState())
    override val state = _state.asStateFlow()

    override fun startListening(languageCode: Language) {
        TODO("Not yet implemented")
    }

    override fun stopListening() {
        TODO("Not yet implemented")
    }

    override fun clearSpokenText() {
        TODO("Not yet implemented")
    }
}