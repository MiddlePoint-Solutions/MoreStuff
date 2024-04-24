package co.softov.morestuff.android.ui.scopes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesFlowUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject

@Composable
fun scopesModel(
    initialState: ScopesState,
    events: Flow<ScopesUiEvent>,
    store: AppStore = koinInject(),
    getScopesFlowUseCase: GetScopesFlowUseCase = koinInject()
): ScopesState {
    var scopesState by remember { mutableStateOf(initialState.scopes) }

    LaunchedEffect(key1 = Unit) {
        getScopesFlowUseCase().collect { newScopes ->
            scopesState = newScopes
        }
    }


    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ScopesUiEvent.CreateScope -> {
                    store.dispatch(ScopeAction.CreateScopeAction(event.name))
                }
                is ScopesUiEvent.DeleteScope -> {
                    store.dispatch(ScopeAction.DeleteScopeAction(event.scopeId))
                }
                is ScopesUiEvent.UpdateScopeName -> {
                    store.dispatch(ScopeAction.UpdateScopeNameAction(event.scopeId, event.newName))
                }
                is ScopesUiEvent.ReorderScope -> {
                    if (event.isFinal) {
                        store.dispatch(ScopeAction.UpdateScopeOrderAction(scopesState))
                    } else {
                        scopesState = scopesState.toMutableList().apply {
                            add(event.toIndex, removeAt(event.fromIndex))
                        }
                    }
                }
            }
        }
    }

    return ScopesState(scopes = scopesState)
}