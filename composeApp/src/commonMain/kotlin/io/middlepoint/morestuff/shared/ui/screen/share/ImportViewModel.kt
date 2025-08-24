package io.middlepoint.morestuff.shared.ui.screen.share

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class ImportViewModel(shareable: Shareable) : MoleculeViewModel<ImportEvent, ImportModel>() {

  override val initialState: ImportModel = ImportModel(shareable)

  @Composable
  override fun models(events: SharedFlow<ImportEvent>): ImportModel {
    return importModel(initialState, events)
  }
}
