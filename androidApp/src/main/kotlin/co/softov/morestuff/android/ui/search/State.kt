package co.softov.morestuff.android.ui.search

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.enums.FilterType
import co.softov.morestuff.android.ui.model.TaskUiModel
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
