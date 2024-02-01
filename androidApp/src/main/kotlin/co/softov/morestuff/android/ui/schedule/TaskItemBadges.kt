package co.softov.morestuff.android.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.model.TaskUiModel

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
                imageVector = Icons.Default.Notes,
                contentDescription = stringResource(R.string.cd_task_message_icon),
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (task.hasSchedule) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_schedule),
                contentDescription = stringResource(R.string.cd_scheduled_task_icon),
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (task.hasReminder) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = stringResource(R.string.cd_task_reminder_icon),
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}