package io.middlepoint.morestuff.shared.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ScopeUiModel(
    val id: Long = 0L,
    val uid: String = "",
    val name: String = "",
    val order: Int = 0
)