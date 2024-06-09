package io.middlepoint.morestuff.shared.ui.components.input.voice

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.service.VoiceToTextParser
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.Flow


class VoiceToTextViewModel(
  private val voiceToTextParser: VoiceToTextParser,
  private val savedState: SavedStateHandle,
  private val logger: Logger,
) : MoleculeViewModel<VoiceToTextUiEvent, VoiceToTextState>() {

  override val initialState: VoiceToTextState = savedState["voice"] ?: VoiceToTextState()

  @Composable
  override fun models(events: Flow<VoiceToTextUiEvent>): VoiceToTextState {
    return voiceToTextModel(
      initialState = initialState,
      events = events,
      voiceToTextParser = voiceToTextParser,
    )
  }

  override fun onSaveState(model: VoiceToTextState) {
    savedState["voice"] = model
    logger.d("language Saved state: $model")
  }
}
