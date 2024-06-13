package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.cd_scheduled_task_icon
import morestuff.shared.generated.resources.cd_task_message_icon
import morestuff.shared.generated.resources.cd_task_reminder_icon
import morestuff.shared.generated.resources.ic_schedule
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TaskItemBadges(
    task: TaskUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (task.extraDetails) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Notes,
                contentDescription = stringResource(Res.string.cd_task_message_icon),
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (task.hasSchedule) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_schedule),
                contentDescription = stringResource(Res.string.cd_scheduled_task_icon),
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (task.hasReminder) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = stringResource(Res.string.cd_task_reminder_icon),
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}