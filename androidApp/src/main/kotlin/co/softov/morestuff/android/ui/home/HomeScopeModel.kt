package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.usecase.scope.GetScopesFlowUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject

@Composable
fun homeScopeModel(
    initialState: HomeScopeState,
    events: Flow<HomeUiEvent>,
    getScopesFlowUseCase: GetScopesFlowUseCase = koinInject()
): HomeScopeState {

    var scopes: List<ScopeDomain> by remember { mutableStateOf(initialState.scopes) }
    var currentScopeId: Long by remember { mutableLongStateOf(initialState.currentScopeId) }

    LaunchedEffect(Unit) {
        getScopesFlowUseCase().collect {
            scopes = it
        }
    }

    LaunchedEffect(Unit) {
        events.collect {
            when(it) {
                HomeUiEvent.ClearTaskSelection -> TODO()
                HomeUiEvent.CompleteSelectedTasks -> TODO()
                HomeUiEvent.DeleteSelectedTasks -> TODO()
                HomeUiEvent.DeleteSelectedTasksFromScope -> TODO()
                is HomeUiEvent.MoveSelectedTasksToScope -> TODO()
                is HomeUiEvent.ScopeSelected -> currentScopeId = it.scopeId
                is HomeUiEvent.SetConfettiEnabled -> TODO()
                is HomeUiEvent.ToggleTaskSelection -> TODO()
                is HomeUiEvent.UndoComplete -> TODO()
                is HomeUiEvent.UndoMoveTasks -> TODO()
            }
        }
    }

    return HomeScopeState(
        currentScopeId = currentScopeId,
        scopes = scopes
    )
}
