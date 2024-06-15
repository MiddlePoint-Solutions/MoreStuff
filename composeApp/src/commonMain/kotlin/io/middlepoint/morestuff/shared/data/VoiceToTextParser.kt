package io.middlepoint.morestuff.shared.data

import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.model.VoiceToTextParserState
import kotlinx.coroutines.flow.StateFlow

interface VoiceToTextParser {
  val state: StateFlow<VoiceToTextParserState>
  fun startListening(languageCode: Language)
  fun stopListening()
  fun clearSpokenText()
}

expect class VoiceToTextParserImpl: VoiceToTextParser
