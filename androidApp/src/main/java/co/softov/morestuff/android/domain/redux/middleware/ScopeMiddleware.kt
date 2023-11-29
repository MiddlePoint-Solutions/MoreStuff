package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.scope.CreateScopeUseCase
import co.softov.morestuff.android.domain.usecase.scope.DeleteScopeUseCase
import co.softov.morestuff.android.domain.usecase.scope.GetScopesUseCase
import co.softov.morestuff.android.domain.usecase.scope.UpdateScopeNameUseCase
import co.softov.morestuff.android.domain.usecase.scope.UpdateScopeOrderUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ScopeAction : Action.FeatureAction() {
    data class CreateScopeAction(val scopeUid: String, val name: String) : ScopeAction()

    data class DeleteScopeAction(val scopeIds: List<Long>) : ScopeAction()
    data class UpdateScopesListAction(val scopes: List<ScopeDomain>) : ScopeAction()
    data class UpdateScopeNameAction(val scopeId: Long, val newName: String) : ScopeAction()
    data class UpdateScopeOrderAction(val scopeId: Long, val newOrder: Long) : ScopeAction()
}

class ScopeMiddleware(
    private val createScopeUseCase: CreateScopeUseCase,
    private val deleteScopeUseCase: DeleteScopeUseCase,
    private val getScopesUseCase: GetScopesUseCase,
    private val updateScopeNameUseCase: UpdateScopeNameUseCase,
    private val updateScopeOrderUseCase: UpdateScopeOrderUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {
            is ScopeAction.CreateScopeAction -> scope.launch {
                createScopeUseCase(action.scopeUid, action.name)
                val updatedScopes = getScopesUseCase()
                dispatch(ScopeAction.UpdateScopesListAction(updatedScopes))
            }

            is ScopeAction.DeleteScopeAction -> scope.launch {
                deleteScopeUseCase(action.scopeIds)
                val updatedScopes = getScopesUseCase()
                dispatch(ScopeAction.UpdateScopesListAction(updatedScopes))
            }
            is ScopeAction.UpdateScopeNameAction -> scope.launch {
                updateScopeNameUseCase(action.scopeId, action.newName)
                dispatch(ScopeAction.UpdateScopeNameAction(action.scopeId,action.newName))

            }
            is ScopeAction.UpdateScopeOrderAction -> scope.launch {
                updateScopeOrderUseCase(action.scopeId, action.newOrder)
                val updatedScopes = getScopesUseCase()
                dispatch(ScopeAction.UpdateScopesListAction(updatedScopes))
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}