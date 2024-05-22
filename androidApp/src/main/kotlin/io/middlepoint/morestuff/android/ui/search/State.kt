package io.middlepoint.morestuff.android.ui.search

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.FilterType
import io.middlepoint.morestuff.android.ui.model.TaskUiModel
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class SearchState(
    val query: String = "",
    val filter: FilterType = FilterType.None,
    var searchResults: List<TaskUiModel> = listOf()
) : Parcelable


sealed class SearchEvent {
    data class SetSearchQuery(val query: String) : SearchEvent()
    data class SetSearchFilter(val filter: FilterType) : SearchEvent()
    data object ClearSearchQuery : SearchEvent()
    data object ResetSearch : SearchEvent()
}
