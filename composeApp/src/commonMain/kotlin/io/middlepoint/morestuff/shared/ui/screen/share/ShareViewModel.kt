package io.middlepoint.morestuff.shared.ui.screen.share

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class ShareViewModel(shareable: Shareable) : MoleculeViewModel<ShareEvent, ShareModel>() {

  override val initialState: ShareModel = ShareModel(shareable)

  @Composable
  override fun models(events: SharedFlow<ShareEvent>): ShareModel {
    return shareModel(initialState, events)
  }
}
