package co.softov.morestuff.android.ui.schedule

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun PriorityItem(
    task: TaskDomain,
    onClick: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
) {
    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "").value
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = elevation)
            .background(MaterialTheme.colorScheme.background)
            .heightIn(80.dp)
            .clickable { onClick(task.id) },
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "${task.title} * score: ${task.priorityScore}",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview
@Composable
fun PriorityItemPreview() {
    MoreStuffTheme {
        PriorityItem(task = TaskDomain(title = "Buy milk & bread"), onClick = {})
    }
}

@Preview
@Composable
fun PriorityItemPreviewDark() {
    MoreStuffTheme(darkTheme = true) {
        PriorityItem(task = TaskDomain(title = "Buy milk & bread"), onClick = {})
    }
}