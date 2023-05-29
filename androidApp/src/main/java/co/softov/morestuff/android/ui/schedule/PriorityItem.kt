package co.softov.morestuff.android.ui.schedule

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.chat.TaskActions
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityItem(
    task: TaskDomain,
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
    taskActions: TaskActions = TaskActions(),
) {

    val elevation = CardDefaults.cardElevation(
        defaultElevation = animateDpAsState(if (isDragging) 12.dp else 0.dp, label = "").value
    )

    Card(
        onClick = { taskActions.taskChatAction(task.id) },
        elevation = elevation,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(56.dp)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "${task.title} * id: ${task.id} * score: ${task.priorityScore}",
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
fun PriorityItemPreview() {
    MoreStuffTheme(darkTheme = true) {
        PriorityItem(task = TaskDomain(title = "Buy milk & bread"))
    }
}