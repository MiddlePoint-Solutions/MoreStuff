package co.softov.morestuff.android.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun PriorityScreen() {
    
}

@Composable
fun PrioritySchedule(
    reorderAction: (Int, Int) -> Unit,
    modifier: Modifier,
    model: ReviewModel
) {
    val state =
        rememberReorderableLazyListState(onMove = { to, from ->
            reorderAction(to.index, from.index)
        })

    LazyColumn(
        state = state.listState,
        modifier = modifier
            .reorderable(state),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(model.roundItems, key = { it.taskId }) {
            ReorderableItem(state, key = it.taskId) { isDragging ->
                ScheduleItem(
                    it,
                    Modifier.detectReorderAfterLongPress(state),
                    isDragging = isDragging
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleItem(
    task: ScheduleListItemViewModel,
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
    itemAction: () -> Unit = {},
) {

    val elevation = if (isDragging) CardDefaults.cardElevation(
        defaultElevation = 16.dp
    ) else CardDefaults.cardElevation()

    Card(
        onClick = itemAction,
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
                text = task.taskTitle,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}