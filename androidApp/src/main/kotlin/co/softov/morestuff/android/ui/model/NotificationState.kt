package co.softov.morestuff.android.ui.model

import android.content.res.Resources
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.model.NotificationState.*
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
sealed class NotificationState(open val action: () -> Unit = {}) : Parcelable {
    @Immutable
    data class Complete(
        override val action: () -> Unit
    ) : NotificationState(action)

    @Immutable
    data class TaskMovedToScope(
        override val action: () -> Unit,
    ) : NotificationState(action)
}

suspend fun NotificationState.show(
    hostState: SnackbarHostState,
    resources: Resources,
): SnackbarResult =
    when (this) {
        is Complete -> hostState.showSnackbar(
            message = resources.getString(R.string.snack_task_completed),
            actionLabel = resources.getString(R.string.undo),
            duration = SnackbarDuration.Short
        )

        is TaskMovedToScope -> hostState.showSnackbar(
            message = resources.getString(R.string.snack_task_moved_to_new_scope),
            actionLabel = resources.getString(R.string.undo),
            duration = SnackbarDuration.Short
        )
    }