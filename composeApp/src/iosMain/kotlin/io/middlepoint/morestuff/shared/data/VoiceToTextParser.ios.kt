package io.middlepoint.morestuff.shared.data


import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.model.VoiceToTextParserState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.setActive
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.localeWithLocaleIdentifier
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import platform.Speech.SFSpeechRecognizerDelegateProtocol
import platform.darwin.NSObject

class VoiceToTextParserImpl : VoiceToTextParser {

  private val _state = MutableStateFlow(VoiceToTextParserState())
  override val state: StateFlow<VoiceToTextParserState>
    get() = _state.asStateFlow()

  private var audioEngine: AVAudioEngine? = null
  private var speechRecognizer: SFSpeechRecognizer? = null
  private var request: SFSpeechAudioBufferRecognitionRequest? = null
  private var recognitionTask: SFSpeechRecognitionTask? = null
  private var silenceJob: Job? = null

  override fun startListening(languageCode: Language) {
    val locale = when (languageCode) {
      Language.English -> NSLocale.localeWithLocaleIdentifier("en-US")
      Language.Spanish -> NSLocale.localeWithLocaleIdentifier("es-ES")
      Language.Hebrew -> NSLocale.localeWithLocaleIdentifier("he-IL")
      Language.Russian -> NSLocale.localeWithLocaleIdentifier("ru-RU")
      Language.Catalan -> NSLocale.localeWithLocaleIdentifier("ca-ES")
      else -> NSLocale.currentLocale
    }
    speechRecognizer = SFSpeechRecognizer(locale = locale)

    if (speechRecognizer == null || speechRecognizer?.isAvailable() != true) {
      _state.value = _state.value.copy(error = "Speech recognition not supported for current language")
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
    try {
      audioEngine = AVAudioEngine()
      request = SFSpeechAudioBufferRecognitionRequest()

      val audioSession = AVAudioSession.sharedInstance()
      audioSession.setCategory(AVAudioSessionCategoryRecord, null)
      audioSession.setMode(AVAudioSessionModeMeasurement, null)
      audioSession.setActive(true, AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation, null)

      val inputNode = audioEngine?.inputNode ?: throw IllegalStateException("Input node not available")
      val recordingFormat = inputNode.outputFormatForBus(0u)

      inputNode.installTapOnBus(0u, 1024u, recordingFormat) { buffer, _ ->
        if (buffer != null && !isBufferSilent(buffer)) {
          resetSilenceTimer()
        }
        buffer?.let { request?.appendAudioPCMBuffer(it) }
      }


      request?.shouldReportPartialResults = false

      recognitionTask = speechRecognizer?.recognitionTaskWithRequest(request!!) { result, error ->
        if (error != null) {
          _state.value = _state.value.copy(error = error.localizedDescription)
          stopListening()
          return@recognitionTaskWithRequest
        }

        if (result != null) {
          val transcription = result.bestTranscription.formattedString
          _state.value = _state.value.copy(spokenText = transcription)

          if (result.isFinal()) {
            stopListening()
          }
        }
      }

      audioEngine?.prepare()
      audioEngine?.startAndReturnError(null)
      _state.value = _state.value.copy(isSpeaking = true)
      startSilenceTimer()
    } catch (e: Exception) {
      _state.value = _state.value.copy(error = "Recording error: ${e.message}")
      stopListening()
    }
  }

  private fun startSilenceTimer() {
    silenceJob?.cancel()
    silenceJob = CoroutineScope(Dispatchers.Main).launch {
      delay(3000)
      stopListening()
    }
  }

  private fun resetSilenceTimer() {
    silenceJob?.cancel()
    startSilenceTimer()
  }

  override fun stopListening() {
    silenceJob?.cancel()
    audioEngine?.stop()
    audioEngine?.inputNode?.removeTapOnBus(0u)
    request?.endAudio()
    recognitionTask?.cancel()
    recognitionTask = null
    _state.value = _state.value.copy(isSpeaking = false)
  }

  override fun clearSpokenText() {
    _state.value = _state.value.copy(spokenText = "")
  }
}


@OptIn(ExperimentalForeignApi::class)
private fun isBufferSilent(buffer: AVAudioPCMBuffer): Boolean {
  val audioDataPointer = buffer.floatChannelData ?: return true
  val audioData = audioDataPointer[0]
  val frameLength = buffer.frameLength.toInt()


  val silenceThreshold = 0.01f


  for (i in 0 until frameLength) {
    if (audioData != null) {
      if (audioData[i] > silenceThreshold || audioData[i] < -silenceThreshold) {
        return false
      }
    }
  }

  return true
}




