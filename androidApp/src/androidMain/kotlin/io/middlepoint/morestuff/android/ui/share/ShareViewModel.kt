package io.middlepoint.morestuff.android.ui.share

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow

class ShareViewModel : MoleculeViewModel<ShareEvent, ShareState>() {

    override val initialState: ShareState = ShareState()

    @Composable
    override fun models(events: Flow<ShareEvent>): ShareState {
        return shareModel(initialState, events)
    }
}
