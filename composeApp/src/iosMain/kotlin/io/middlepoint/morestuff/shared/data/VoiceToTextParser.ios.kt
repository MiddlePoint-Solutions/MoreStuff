package io.middlepoint.morestuff.shared.data

import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.model.VoiceToTextParserState
import kotlinx.coroutines.flow.StateFlow

actual class VoiceToTextParserImpl : VoiceToTextParser {
    override val state: StateFlow<VoiceToTextParserState>
        get() = TODO("Not yet implemented")

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