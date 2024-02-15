package co.softov.morestuff.android.ui.scopes

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent

@Immutable
sealed class ScopesUiEvent : BaseViewEvent {
    data class CreateScope(
        val name: String,
        val taskIds: List<Long> = listOf()
    ) : ScopesUiEvent()

    data class DeleteScope(val scopeId: Long) : ScopesUiEvent()
    data class UpdateScopeName(val scopeId: Long, val newName: String) : ScopesUiEvent()
    data class ReorderScope(val fromIndex: Int, val toIndex: Int, val isFinal: Boolean) :
        ScopesUiEvent()
}
