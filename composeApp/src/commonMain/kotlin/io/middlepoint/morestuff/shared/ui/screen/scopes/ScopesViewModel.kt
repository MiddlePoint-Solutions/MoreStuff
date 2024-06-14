package io.middlepoint.morestuff.shared.ui.screen.scopes

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.Flow


class ScopesViewModel: MoleculeViewModel<ScopesUiEvent, ScopesState>() {

  override val initialState: ScopesState = ScopesState()

  @Composable
  override fun models(events: Flow<ScopesUiEvent>): ScopesState {
    return scopesModel(
      initialState = initialState,
      events = events,
    )
  }

}
