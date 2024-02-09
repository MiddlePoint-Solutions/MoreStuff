package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.Composable
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import kotlinx.coroutines.flow.Flow

class ScopeTasksPresenter(
    private val scopeId: Long,
    private val selectedTasksFlow: Flow<List<Long>>,
) : MoleculeViewModel<Nothing, ScopeTasksModels>() {

    override val initialState: ScopeTasksModels = ScopeTasksModels.Loading

    @Composable
    override fun models(events: Flow<Nothing>): ScopeTasksModels {
        return scopeTasksModel(scopeId, selectedTasksFlow)
    }
}