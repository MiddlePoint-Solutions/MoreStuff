package co.softov.morestuff.android.app.features

import kotlinx.coroutines.flow.StateFlow

interface VoiceToTextInterface {
    val state: StateFlow<VoiceToTextParserState>
    fun startListening()
    fun stopListening()
}
