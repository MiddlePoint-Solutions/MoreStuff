package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class ScopeAction : Action.FeatureAction() {
    data class CreateScopeAction(val name: String) : ScopeAction()
    data class DeleteScopeAction(val scopeId: Uuid) : ScopeAction()
    data class UpdateScopeNameAction(val scopeId: Uuid, val newName: String) : ScopeAction()
    data class UpdateScopeOrderAction(val scopes: List<Scope>) : ScopeAction()
}