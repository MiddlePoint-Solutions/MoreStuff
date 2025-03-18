package io.middlepoint.morestuff.shared.ui.components.input.voice

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.data.VoiceToTextParser
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow


class VoiceToTextViewModel(
  private val voiceToTextParser: VoiceToTextParser,
) : MoleculeViewModel<VoiceToTextUiEvent, VoiceToTextState>() {

  override val initialState: VoiceToTextState = VoiceToTextState()

  @Composable
  override fun models(events: SharedFlow<VoiceToTextUiEvent>): VoiceToTextState {
    return voiceToTextModel(
      initialState = initialState,
      events = events,
      voiceToTextParser = voiceToTextParser,
    )
  }

}
