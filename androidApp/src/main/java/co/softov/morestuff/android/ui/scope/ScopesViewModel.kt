package co.softov.morestuff.android.ui.scope

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.scopeAll
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesFlowUseCase
import co.softov.morestuff.android.ui.scope.ScopesUiEvent.CreateScope
import co.softov.morestuff.android.ui.scope.ScopesUiEvent.DeleteScope
import co.softov.morestuff.android.ui.scope.ScopesUiEvent.ReorderScope
import co.softov.morestuff.android.ui.scope.ScopesUiEvent.UpdateScopeName
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class ScopesViewModel(
    getScopesFlowUseCase: GetScopesFlowUseCase,
) : NoStateViewModel() {

    private val scopes = getScopesFlowUseCase()
        .onEach { scopes ->
            Timber.d("reorderScope, update: ${scopes.map { it.name }}")
            scopesState = scopes
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf(scopeAll)
        )

    var scopesState by mutableStateOf<List<ScopeDomain>>(listOf())
        private set

    fun handleEvent(event: ScopesUiEvent) {
        when (event) {
            is CreateScope -> {
                dispatchAppStoreAction(ScopeAction.CreateScopeAction(event.uid, event.name))
            }

            is UpdateScopeName -> {
                dispatchAppStoreAction(
                    ScopeAction.UpdateScopeNameAction(
                        event.scopeId,
                        event.newName
                    )
                )
            }

            is DeleteScope -> {
                dispatchAppStoreAction(ScopeAction.DeleteScopeAction(event.scopeId))
            }

            is ReorderScope -> {
                when {
                    event.isFinal -> if (event.fromIndex != event.toIndex) {
                        dispatchAppStoreAction((ScopeAction.UpdateScopeOrderAction(scopesState)))
                    }
                    else -> updateScopeOrder(event.fromIndex, event.toIndex)
                }
            }
        }
    }

    private fun updateScopeOrder(fromPosition: Int, toPosition: Int) {
        scopesState = scopesState.toMutableList().apply {
            add(toPosition, removeAt(fromPosition))
        }
    }

}
