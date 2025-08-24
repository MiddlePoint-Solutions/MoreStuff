package io.middlepoint.morestuff.shared.ui.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.Uuid

@Immutable
data class ReviewItemUiModel(
    val id: Uuid,
    val createTime: String,
    val title: String,
    val position: String,
    val priorityScore: Long,
    var isCompleted: Boolean,
    val extraDetails: Boolean,
    val messages: List<MessageUiModel>
)