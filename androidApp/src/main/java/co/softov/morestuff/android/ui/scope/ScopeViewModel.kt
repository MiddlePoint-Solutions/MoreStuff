package co.softov.morestuff.android.ui.scope

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.usecase.scope.GetScopesUseCase
import co.softov.morestuff.android.domain.usecase.task.RemoveTaskFromScopeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class ScopeViewModel(
    private val removeTaskFromScopeUseCase: RemoveTaskFromScopeUseCase,
    private val getScopesUseCase: GetScopesUseCase,
    private val taskRepository: TaskRepository,
) : BaseViewModel<ScopeUiModel, ScopeUiEvent>(ScopeUiModel()) {

    private val _uiState = MutableStateFlow(ScopeUiModel())
    val uiState: StateFlow<ScopeUiModel> = _uiState.asStateFlow()

    init {
        handleEvent(ScopeUiEvent.LoadScopes)
    }

    override fun onReduceState(event: ScopeUiEvent): ScopeUiModel {
        return when (event) {
            is ScopeUiEvent.LoadScopes -> {
                loadDataAndPrepare()
                state
            }

            is ScopeUiEvent.CreateScope -> {
                createScope(event.uid, event.name)
                state
            }

            is ScopeUiEvent.SelectScope -> {
                selectScope(event.scopeId)
                state.copy(selectedScopeId = event.scopeId)
            }
            is ScopeUiEvent.DeleteScopes -> {
                deleteScopes(event.scopeIds)
                state
            }
            is ScopeUiEvent.UpdateScopeName -> {
                updateScopeName(event.scopeId, event.newName)
                state
            }
            is ScopeUiEvent.ReorderScopes -> {
                val reorderedScopes = state.scopes.sortedBy { scope ->
                    event.newOrder.indexOf(scope.scopeId)
                }
                state.copy(scopes = reorderedScopes)
            }
        }
    }

    fun handleEvent(event: ScopeUiEvent) {
        when (event) {
            is ScopeUiEvent.LoadScopes -> loadDataAndPrepare()
            is ScopeUiEvent.CreateScope -> createScope(event.uid, event.name)
            is ScopeUiEvent.SelectScope -> selectScope(event.scopeId)
            is ScopeUiEvent.DeleteScopes -> deleteScopes(event.scopeIds)
            is ScopeUiEvent.UpdateScopeName -> updateScopeName(event.scopeId, event.newName)
            is ScopeUiEvent.ReorderScopes -> reorderScopes(event.newOrder)
        }
    }

    private fun loadDataAndPrepare() {
        viewModelScope.launch {
            val loadedScopes = getScopesUseCase.invoke()
            _uiState.value = _uiState.value.copy(
                scopes = loadedScopes,
                selectedScopeId = loadedScopes.firstOrNull()?.scopeId
            )
        }
    }

    private fun createScope(uid: String, name: String) {
        viewModelScope.launch {
            dispatchAppStoreAction(ScopeAction.CreateScopeAction(uid, name))
            delay(500)
            loadDataAndPrepare()
        }
    }

    private fun selectScope(scopeId: Long) {
        Timber.d("Scope seleccionado: $scopeId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedScopeId = scopeId)
            taskRepository.updateCurrentScopeId(scopeId)
        }
    }


    fun deleteScopes(scopeIds: List<Long>) {
        viewModelScope.launch {
            dispatchAppStoreAction(ScopeAction.DeleteScopeAction(scopeIds))
            delay(500)
            loadDataAndPrepare()
        }
    }


    fun removeTaskFromScope(taskId: Long, scopeId: Long) {
        viewModelScope.launch {
            removeTaskFromScopeUseCase(taskId, scopeId)
        }
    }
    private fun updateScopeName(scopeId: Long, newName: String) {
        viewModelScope.launch {
            dispatchAppStoreAction(ScopeAction.UpdateScopeNameAction(scopeId, newName))
            delay(500)
            loadDataAndPrepare()
        }
    }
    private fun reorderScopes(newOrder: List<Long>) {
        viewModelScope.launch {
            newOrder.forEachIndexed { index, scopeId ->
                dispatchAppStoreAction(ScopeAction.UpdateScopeOrderAction(scopeId, index.toLong()))
            }
            delay(500)

            loadDataAndPrepare()
        }
    }


}
