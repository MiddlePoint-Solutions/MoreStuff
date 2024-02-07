package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.model.ScopeDomain

@Immutable
sealed class NotificationState {
    @Immutable
    data class Complete(
        val action: () -> Unit
    ) : NotificationState()

    @Immutable
    data class TaskMovedToNewScope(
        val from: ScopeDomain,
        val action: () -> Unit,
    ) : NotificationState()
}