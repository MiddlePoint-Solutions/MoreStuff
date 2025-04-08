package io.middlepoint.morestuff.shared.ui.screen.scopes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.ScopeAction.*
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCase
import kotlinx.coroutines.flow.SharedFlow
import org.koin.compose.koinInject

@Composable
fun scopesModel(
  initialState: ScopesState,
  events: SharedFlow<ScopesUiEvent>,
  store: AppStore = koinInject(),
  getScopesFlowUseCase: GetScopesFlowUseCase = koinInject()
): ScopesState {
  var scopesState by remember { mutableStateOf(initialState.scopes) }

  LaunchedEffect(Unit) {
    getScopesFlowUseCase().collect { newScopes ->
      scopesState = newScopes
    }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        is ScopesUiEvent.CreateScope -> {
          store.dispatch(CreateScopeAction(event.name))
        }

        is ScopesUiEvent.DeleteScope -> {
          store.dispatch(DeleteScopeAction(event.scopeId))
        }

        is ScopesUiEvent.UpdateScopeName -> {
          store.dispatch(UpdateScopeNameAction(event.scopeId, event.newName))
        }

        is ScopesUiEvent.ReorderScopes -> {
          store.dispatch(UpdateScopeOrderAction(event.scopes))
        }
      }
    }
  }

  return ScopesState(scopes = scopesState)
}