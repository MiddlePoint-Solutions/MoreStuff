package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.redux.state.PriorityState
import co.softov.morestuff.android.domain.redux.state.ReviewState
import co.softov.morestuff.android.domain.redux.state.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AppState(
    val user: UserState = UserState(),
    val priorityState: PriorityState = PriorityState(),
    val reviewState: ReviewState = ReviewState()
)

val AppState.snoozeLimit: Int get() = user.taskSnoozeLimit
val AppState.currentPriority: Priority get() = priorityState.current

fun PriorityState.asStateFlow(): StateFlow<PriorityState> = MutableStateFlow(this)