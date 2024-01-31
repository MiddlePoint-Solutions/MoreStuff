package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction.CreateScopeAction
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction.DeleteScopeAction
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction.UpdateScopeNameAction
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction.UpdateScopeOrderAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.InitStoreAction
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.scope.CreateScopeUseCase
import co.softov.morestuff.android.domain.usecase.scope.DeleteScopeUseCase
import co.softov.morestuff.android.domain.usecase.scope.InitScopesUseCase
import co.softov.morestuff.android.domain.usecase.scope.UpdateScopeNameUseCase
import co.softov.morestuff.android.domain.usecase.scope.UpdateScopesOrderUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ScopeAction : Action.FeatureAction() {
    data class CreateScopeAction(val name: String) : ScopeAction()
    data class DeleteScopeAction(val scopeId: Long) : ScopeAction()
    data class UpdateScopeNameAction(val scopeId: Long, val newName: String) : ScopeAction()
    data class UpdateScopeOrderAction(val scopes: List<ScopeDomain>) : ScopeAction()
}

class ScopeMiddleware(
    private val initScopesUseCase: InitScopesUseCase,
    private val createScopeUseCase: CreateScopeUseCase,
    private val deleteScopeUseCase: DeleteScopeUseCase,
    private val updateScopeNameUseCase: UpdateScopeNameUseCase,
    private val updateScopesOrderUseCase: UpdateScopesOrderUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {

        when (action) {

            is InitStoreAction -> scope.launch {
                initScopesUseCase()
            }

            is CreateScopeAction -> scope.launch {
                createScopeUseCase(action.name)
            }

            is DeleteScopeAction -> scope.launch {
                deleteScopeUseCase(action.scopeId)
            }

            is UpdateScopeNameAction -> scope.launch {
                updateScopeNameUseCase(action.scopeId, action.newName)
            }

            is UpdateScopeOrderAction -> scope.launch {
                updateScopesOrderUseCase(action.scopes)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}