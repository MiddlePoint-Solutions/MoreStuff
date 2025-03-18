package io.middlepoint.morestuff.shared.ui.components.input

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.domain.model.ChatContext
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class UserInputViewModel(
  private val context: ChatContext
) : MoleculeViewModel<UserInputEvent, UserInputState>() {

  override val initialState: UserInputState = UserInputState()

  @Composable
  override fun models(events: SharedFlow<UserInputEvent>): UserInputState {
    return userInputModel(initialState, context, events)
  }
}
