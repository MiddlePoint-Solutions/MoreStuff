package io.middlepoint.morestuff.shared.ui.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.ui.components.PriorityItem
import io.middlepoint.morestuff.shared.ui.compose.simpleVerticalScrollbar
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.theme.divider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


val logger = Logger.withTag("ScopeContent")

@OptIn(FlowPreview::class)
@Composable
fun ScopeContent(
    tasks: List<TaskUiModel>,
    selectedTasks: List<Long>,
    onItemClick: (taskId: Long) -> Unit,
    onItemLongClick: (taskId: Long) -> Unit,
    onReorder: (updatedTasks: List<TaskUiModel>) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {

    var reorderList by remember(tasks) { mutableStateOf(tasks) }

    val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
        val newList = reorderList.toMutableList().apply {
            val movedTask = removeAt(from.index)
            add(to.index, movedTask)
        }

        logger.d("Moving task: ${reorderList[from.index].title} from position ${from.index} to ${to.index}")

        reorderList = newList
    }

    LaunchedEffect(Unit) {
        snapshotFlow { reorderList }
            .debounce(300)
            .collectLatest { updatedList ->
                if (updatedList != tasks) {
                    logger.d("Final reordering triggered")
                    onReorder(updatedList)
                }
            }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .simpleVerticalScrollbar(listState),
        state = listState,
    ) {
        itemsIndexed(
            items = reorderList,
            key = { _, task -> task.id }
        ) { index, item ->
            val isLast by remember(index) {
                derivedStateOf { index == tasks.lastIndex }
            }
            val selected by remember(selectedTasks) {
                derivedStateOf { selectedTasks.contains(item.id) }
            }

            ReorderableItem(reorderableState, key = item.id) { isDragging ->
                PriorityItem(
                    task = item,
                    selected = selected,
                    onClick = { onItemClick(item.id) },
                    onLongClick = { onItemLongClick(item.id) },
                    modifier = Modifier.fillMaxWidth(),
                    isDragging = isDragging,
                    handleModifier = if (selected) Modifier.draggableHandle(true) else Modifier
                )
            }

            if (!isLast) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = MaterialTheme.colorScheme.divider
                    )
                }
            }
        }
    }
}
