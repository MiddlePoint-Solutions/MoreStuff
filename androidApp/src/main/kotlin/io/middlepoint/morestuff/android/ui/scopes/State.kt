package io.middlepoint.morestuff.android.ui.scopes

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
@Immutable
data class ScopesState(
    val scopes: List<ScopeDomain> = listOf()
) : Parcelable


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
