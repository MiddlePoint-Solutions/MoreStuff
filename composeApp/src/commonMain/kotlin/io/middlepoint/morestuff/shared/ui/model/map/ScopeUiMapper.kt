package io.middlepoint.morestuff.shared.ui.model.map

import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.ui.model.ScopeUiModel

class ScopeUiMapper {
    fun map(input: ScopeDomain): ScopeUiModel = ScopeUiModel(
        id = input.id,
        uid = input.uid,
        name = input.name,
        order = input.order
    )

    fun map(input: List<ScopeDomain>): List<ScopeUiModel> =
        input.map { map(it) }
}