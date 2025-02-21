package io.middlepoint.morestuff.shared.ui.screen.schedule

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class ScopeTasksViewModel(
    private val scopeId: Long,
    ) : MoleculeViewModel<ScopeTasksEvent, ScopeTasksModels>() {

    override val initialState: ScopeTasksModels = ScopeTasksModels.Loading

    @Composable
    override fun models(events: SharedFlow<ScopeTasksEvent>): ScopeTasksModels {
        return scopeTasksModel( scopeId = scopeId, events = events)
    }
}