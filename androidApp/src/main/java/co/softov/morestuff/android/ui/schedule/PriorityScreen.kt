package co.softov.morestuff.android.ui.schedule

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityScreen(
    viewModel: PriorityViewModel = koinViewModel()
) {

    val model by viewModel.uiModel.collectAsState()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(text = "Priority") }
        )
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Box {
                PrioritySchedule(
                    items = model.items,
                    reorderAction = viewModel::reorderTaskItem
                )
            }
        }
    }

}

@Composable
fun PrioritySchedule(
    items: List<ScheduleListItemViewModel>,
    reorderAction: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    val state = rememberReorderableLazyListState(
        onMove = { to, from ->
            reorderAction(to.index, from.index)
        }
    )

    LazyColumn(
        state = state.listState,
        modifier = modifier
            .reorderable(state),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items, key = { it.taskId }) {
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

    val elevation = CardDefaults.cardElevation(
        defaultElevation = animateDpAsState(if (isDragging) 12.dp else 0.dp).value
    )

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