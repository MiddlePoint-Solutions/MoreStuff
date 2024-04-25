package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class ReviewItemUiModel(
    val id: Long,
    val createTime: String,
    val title: String,
    val position: String,
    val priorityScore: Long,
    var isCompleted: Boolean,
    val extraDetails: Boolean,
):Parcelable