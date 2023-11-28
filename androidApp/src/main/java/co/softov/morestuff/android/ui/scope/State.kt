package co.softov.morestuff.android.ui.scope

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.domain.model.ScopeDomain


@Immutable
data class ScopeUiModel(
    val selectedScopeId: Long? = null,
    val scopes: List<ScopeDomain> = emptyList(),
) : BaseViewState



sealed class ScopeUiEvent : BaseViewEvent {
    data object LoadScopes : ScopeUiEvent()
    data class CreateScope(val uid: String, val name: String) : ScopeUiEvent()
    data class SelectScope(val scopeId: Long) : ScopeUiEvent()
}

