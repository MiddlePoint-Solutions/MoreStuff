package io.middlepoint.morestuff.android.ui.schedule

import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow

class ScopeTasksPresenter(
    private val scopeId: Long,
) : MoleculeViewModel<Nothing, ScopeTasksModels>() {

    override val initialState: ScopeTasksModels = ScopeTasksModels.Loading

    @Composable
    override fun models(events: Flow<Nothing>): ScopeTasksModels {
        return scopeTasksModel(scopeId)
    }
}