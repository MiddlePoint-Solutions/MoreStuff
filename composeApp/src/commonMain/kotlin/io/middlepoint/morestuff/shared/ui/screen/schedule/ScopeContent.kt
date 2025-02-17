package io.middlepoint.morestuff.shared.ui.screen.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.components.PriorityItem
import io.middlepoint.morestuff.shared.ui.compose.simpleVerticalScrollbar
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.theme.divider
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScopeContent(
    tasks: List<TaskUiModel>,
    selectedTasks: List<Long>,
    onItemClick: (taskId: Long) -> Unit,
    onItemLongClick: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    var taskList by remember { mutableStateOf(tasks) }

    val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
        taskList = taskList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .simpleVerticalScrollbar(listState),
        state = listState,
    ) {
        itemsIndexed(
            items = taskList,
            key = { _, task -> task.id }
        ) { index, item ->

            val isLast by remember(index) {
                derivedStateOf { index == taskList.lastIndex }
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
