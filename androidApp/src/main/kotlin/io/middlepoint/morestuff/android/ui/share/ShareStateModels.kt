package io.middlepoint.morestuff.android.ui.share

import io.middlepoint.morestuff.android.ui.model.TaskUiModel

data class ShareState(
    val tasks: List<TaskUiModel> = listOf(),
    val searchResults: List<TaskUiModel> = listOf()
)

sealed class ShareEvent {
    data object ClearSearchQuery : ShareEvent()
    data class UpdateSearchQuery(val query: String) : ShareEvent()
}