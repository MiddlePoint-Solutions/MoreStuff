package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class ScopeAction : Action.FeatureAction() {
    data class CreateScopeAction(val name: String) : ScopeAction()
    data class DeleteScopeAction(val scopeId: Long) : ScopeAction()
    data class UpdateScopeNameAction(val scopeId: Long, val newName: String) : ScopeAction()
    data class UpdateScopeOrderAction(val scopes: List<ScopeDomain>) : ScopeAction()
}