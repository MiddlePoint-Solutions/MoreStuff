package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class TaskUiModel(
    val id: Long = 0L,
    val title: String = "",
    val createTime: String = "",
    val completeTime: String = "",
    val isComplete: Boolean = false,
    val priorityScore: Long = 0L,
    val position: Int = 0,
    val extraDetails: Boolean = false,
    val hasSchedule: Boolean = false,
    val hasReminder: Boolean = false,
): Parcelable
