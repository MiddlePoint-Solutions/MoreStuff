package io.middlepoint.morestuff.shared.ui.components.input

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.Flow

class UserInputViewModel : MoleculeViewModel<UserInputEvent, UserInputState>() {

  override val initialState: UserInputState = UserInputState()

  @Composable
  override fun models(events: Flow<UserInputEvent>): UserInputState {
    return userInputModel(initialState, events)
  }
}
