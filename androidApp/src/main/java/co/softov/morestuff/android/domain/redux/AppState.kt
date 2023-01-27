package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.redux.state.PriorityState
import co.softov.morestuff.android.domain.redux.state.ReviewState
import co.softov.morestuff.android.domain.redux.state.SettingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AppState(
    val settingState: SettingState = SettingState(),
    val priorityState: PriorityState = PriorityState(),
    val reviewState: ReviewState = ReviewState()
)

val AppState.dailySnoozeLimit: Int get() = settingState.dailySnoozeLimit
val AppState.currentPriority: Priority get() = priorityState.current

fun PriorityState.asStateFlow(): StateFlow<PriorityState> = MutableStateFlow(this)