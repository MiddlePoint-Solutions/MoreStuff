package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class ReviewItemUiModel(
    val id: Long,
    val createTime: String,
    val title: String,
    val position: String,
    val priorityScore: Long,
    var isCompleted: Boolean,
    val extraDetails: Boolean,
)