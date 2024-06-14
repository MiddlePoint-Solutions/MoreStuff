package io.middlepoint.morestuff.shared.domain.redux.middleware

import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScopeAction.CreateScopeAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScopeAction.DeleteScopeAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScopeAction.UpdateScopeNameAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScopeAction.UpdateScopeOrderAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.InitStoreAction
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.usecase.scope.CreateScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.DeleteScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.InitScopesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.UpdateScopeNameUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.UpdateScopesOrderUseCase
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