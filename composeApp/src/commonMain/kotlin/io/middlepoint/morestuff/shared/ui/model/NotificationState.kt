package io.middlepoint.morestuff.shared.ui.model

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.ui.model.NotificationState.Complete
import io.middlepoint.morestuff.shared.ui.model.NotificationState.TaskMovedToScope
import kotlinx.serialization.Serializable
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.snack_task_completed
import morestuff.composeapp.generated.resources.snack_task_moved_to_new_scope
import morestuff.composeapp.generated.resources.undo
import org.jetbrains.compose.resources.getString

@Serializable
@Immutable
sealed class NotificationState(open val action: () -> Unit = {}) {

    @Immutable
    data class Complete(override val action: () -> Unit) : NotificationState(action)

    @Immutable
    data class TaskMovedToScope(val scopeTitle: String) : NotificationState()
}

suspend fun NotificationState.show(
    hostState: SnackbarHostState,
): SnackbarResult =
    when (this) {
        is Complete -> hostState.showSnackbar(
            message = getString(Res.string.snack_task_completed),
            actionLabel = getString(Res.string.undo),
            duration = SnackbarDuration.Short
        )

        is TaskMovedToScope -> hostState.showSnackbar(
            message = getString(Res.string.snack_task_moved_to_new_scope).let {
                "$it $scopeTitle"
            },
            duration = SnackbarDuration.Short
        )
    }