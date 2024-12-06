package io.middlepoint.morestuff.shared.ui.screen.schedule

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

class ScopeTasksPresenter(
    private val scopeId: Long,
) : MoleculeViewModel<Nothing, ScopeTasksModels>() {

    override val initialState: ScopeTasksModels = ScopeTasksModels.Loading

    @Composable
    override fun models(events: SharedFlow<Nothing>): ScopeTasksModels {
        return scopeTasksModel(scopeId)
    }
}