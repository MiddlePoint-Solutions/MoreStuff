package co.softov.morestuff.android.ui.model

data class ReviewItemUiModel(
    val id: Long,
    val createTime: String,
    val title: String,
    val priorityScore: Long,
    var isCompleted: Boolean
)