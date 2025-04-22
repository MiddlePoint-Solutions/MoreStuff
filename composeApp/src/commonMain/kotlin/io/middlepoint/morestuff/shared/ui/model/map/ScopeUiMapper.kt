package io.middlepoint.morestuff.shared.ui.model.map

import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.ui.model.ScopeUiModel

class ScopeUiMapper {
    fun map(input: Scope): ScopeUiModel = ScopeUiModel(
        id = input.id,
        name = input.name,
        order = input.order
    )

    fun map(input: List<Scope>): List<ScopeUiModel> =
        input.map { map(it) }
}