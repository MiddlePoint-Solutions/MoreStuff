package co.softov.morestuff.android.ui.input

import androidx.compose.runtime.Composable
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import co.softov.morestuff.android.domain.model.ChatContext
import kotlinx.coroutines.flow.Flow

class UserInputViewModel : MoleculeViewModel<UserInputEvent, UserInputState>() {

  override val initialState: UserInputState = UserInputState()

  @Composable
  override fun models(events: Flow<UserInputEvent>): UserInputState {
    return userInputModel(initialState, events)
  }
}
