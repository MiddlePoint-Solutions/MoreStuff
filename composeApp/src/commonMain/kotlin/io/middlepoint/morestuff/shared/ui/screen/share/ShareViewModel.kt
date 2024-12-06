package io.middlepoint.morestuff.shared.ui.screen.share

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class ShareViewModel : MoleculeViewModel<ShareEvent, ShareState>() {

    override val initialState: ShareState = ShareState()

    @Composable
    override fun models(events: SharedFlow<ShareEvent>): ShareState {
        return shareModel(initialState, events)
    }
}
