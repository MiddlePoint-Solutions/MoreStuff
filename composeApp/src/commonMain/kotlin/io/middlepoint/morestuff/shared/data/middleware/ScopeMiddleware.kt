package io.middlepoint.morestuff.shared.data.middleware

import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.action.ScopeAction.CreateScopeAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScopeAction.DeleteScopeAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScopeAction.UpdateScopeNameAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScopeAction.UpdateScopeOrderAction
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