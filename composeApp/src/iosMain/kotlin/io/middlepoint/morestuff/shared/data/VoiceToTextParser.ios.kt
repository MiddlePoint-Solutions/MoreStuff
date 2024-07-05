package io.middlepoint.morestuff.shared.data

import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.model.VoiceToTextParserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.AVFoundation.*
import platform.Foundation.*
import platform.Speech.*
import kotlinx.cinterop.*
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.setActive
import platform.darwin.NSObject

class VoiceToTextParserImpl : VoiceToTextParser {

  private val _state = MutableStateFlow(VoiceToTextParserState())
  override val state: StateFlow<VoiceToTextParserState>
    get() = _state.asStateFlow()

  private var audioEngine: AVAudioEngine? = null
  private var speechRecognizer: SFSpeechRecognizer? = null
  private var request: SFSpeechAudioBufferRecognitionRequest? = null
  private var recognitionTask: SFSpeechRecognitionTask? = null

  override fun startListening(languageCode: Language) {
    val locale = when (languageCode) {
      Language.English -> NSLocale.localeWithLocaleIdentifier("en-US")
      Language.Spanish -> NSLocale.localeWithLocaleIdentifier("es-ES")
      // Add more languages as needed
      else -> NSLocale.currentLocale
    }
    speechRecognizer = SFSpeechRecognizer(locale = locale)

    if (speechRecognizer == null || speechRecognizer?.isAvailable() != true) {
      _state.value =
        _state.value.copy(error = "Speech recognition not supported for current language")
      return
    }

    speechRecognizer?.delegate = object : NSObject(), SFSpeechRecognizerDelegateProtocol {}

    SFSpeechRecognizer.requestAuthorization { status ->
      when (status) {
        SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized -> {
          this@VoiceToTextParserImpl.startRecording()
        }

        else -> {
          _state.value = _state.value.copy(error = "Speech recognition authorization denied")
        }
      }
    }
  }

  @OptIn(ExperimentalForeignApi::class)
  private fun startRecording() {
    audioEngine = AVAudioEngine()
    request = SFSpeechAudioBufferRecognitionRequest()

    val audioSession = AVAudioSession.sharedInstance()
    audioSession.setCategory(AVAudioSessionCategoryRecord, null)
    audioSession.setMode(AVAudioSessionModeMeasurement, null)
    audioSession.setActive(
      active = true,
      withOptions = AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation,
      error = null
    )

    val inputNode = audioEngine?.inputNode
    request?.shouldReportPartialResults = false
    request?.requiresOnDeviceRecognition = true
    recognitionTask = speechRecognizer?.recognitionTaskWithRequest(
      request!!,
      object : NSObject(), SFSpeechRecognitionTaskDelegateProtocol {

        override fun speechRecognitionTask(
          task: SFSpeechRecognitionTask,
          didFinishRecognition: SFSpeechRecognitionResult
        ) {

          _state.value =
            _state.value.copy(spokenText = didFinishRecognition.bestTranscription.formattedString)

          if (task.error != null || didFinishRecognition.final) {
            stopListening()
          }
        }

      })

    val recordingFormat = inputNode?.outputFormatForBus(0u)
    inputNode?.installTapOnBus(0u, 1024u, recordingFormat!!) { buffer, _ ->
      buffer?.let { request?.appendAudioPCMBuffer(it) }
    }

    audioEngine?.prepare()
    audioEngine?.startAndReturnError(null)
    _state.value = _state.value.copy(isSpeaking = true)
  }

  override fun stopListening() {
    audioEngine?.stop()
    request?.endAudio()
    recognitionTask?.cancel()
    _state.value = _state.value.copy(isSpeaking = false)
  }

  override fun clearSpokenText() {
    _state.value = _state.value.copy(spokenText = "")
  }
}
