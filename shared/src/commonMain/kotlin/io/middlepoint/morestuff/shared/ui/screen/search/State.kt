package io.middlepoint.morestuff.shared.ui.screen.search

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.FilterType
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

@Immutable
data class SearchState(
    val query: String = "",
    val filter: FilterType = FilterType.None,
    var searchResults: List<TaskUiModel> = listOf()
)


sealed class SearchEvent {
    data class SetSearchQuery(val query: String) : SearchEvent()
    data class SetSearchFilter(val filter: FilterType) : SearchEvent()
    data object ClearSearchQuery : SearchEvent()
    data object ResetSearch : SearchEvent()
}
