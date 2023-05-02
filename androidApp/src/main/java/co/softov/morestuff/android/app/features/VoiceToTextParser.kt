package co.softov.morestuff.android.app.features

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class VoiceToTextParser(
    private val app: Application,
) : RecognitionListener, VoiceToTextInterface {
    private val _state = MutableStateFlow(VoiceToTextParserState())
    override val state = _state.asStateFlow()
    val recognizer = SpeechRecognizer.createSpeechRecognizer(app)
    private val languageCode = getDeviceLanguageCode()

    override fun startListening() {
        _state.update { VoiceToTextParserState() }

        if (!SpeechRecognizer.isRecognitionAvailable(app)) {
            _state.update {
                it.copy(
                    error = "Speech recognition is not available"
                )
            }
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE,languageCode )
        }

        recognizer.setRecognitionListener(this)

        recognizer.startListening(intent)

        _state.update {
            it.copy(
                isSpeaking = true
            )
        }
    }

    override fun stopListening() {
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
        recognizer.stopListening()
    }


    override fun onReadyForSpeech(params: Bundle?) {
        _state.update {
            it.copy(
                error = null
            )
        }
    }

    override fun onBeginningOfSpeech() = Unit

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        _state.update {
            it.copy(
                isSpeaking = false
            )
        }
    }

    override fun onError(error: Int) {
        if (error == SpeechRecognizer.ERROR_CLIENT) {
            return
        }
        _state.update {
            it.copy(
                error = "Error: $error"
            )
        }
    }

    override fun onResults(results: Bundle?) {
        results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { text ->
                _state.update {
                    it.copy(
                        spokenText = text
                    )
                }

            }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        partialResults
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { text ->
                _state.update {
                    it.copy(
                        spokenText = text
                    )
                }
            }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        TODO("Not yet implemented")
    }
    private fun getDeviceLanguageCode(): String {
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            app.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            app.resources.configuration.locale
        }
        return locale.language
    }
}

data class VoiceToTextParserState(
    val spokenText: String = "",
    val isSpeaking: Boolean = false,
    val error: String? = null,
)

