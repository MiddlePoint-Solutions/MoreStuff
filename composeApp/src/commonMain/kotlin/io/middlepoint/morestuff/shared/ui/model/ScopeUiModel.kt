package io.middlepoint.morestuff.shared.ui.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ScopeUiModel(
    val id: Uuid = Uuid.empty(),
    val name: String = "",
    val order: Int = 0
)