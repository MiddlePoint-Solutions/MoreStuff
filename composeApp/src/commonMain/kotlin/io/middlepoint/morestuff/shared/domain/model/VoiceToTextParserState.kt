package io.middlepoint.morestuff.shared.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class VoiceToTextParserState(
    val spokenText: String = "",
    val isSpeaking: Boolean = false,
    val error: String? = null,
)