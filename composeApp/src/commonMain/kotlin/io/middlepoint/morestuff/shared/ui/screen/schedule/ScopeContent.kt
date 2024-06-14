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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.middlepoint.morestuff.shared.ui.components.PriorityItem
import io.middlepoint.morestuff.shared.ui.compose.simpleVerticalScrollbar
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.theme.divider

@Composable
fun ScopeContent(
    tasks: List<TaskUiModel>,
    selectedTasks: List<Long>,
    onItemClick: (taskId: Long) -> Unit,
    onItemLongClick: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .simpleVerticalScrollbar(listState),
        state = listState,
    ) {
        itemsIndexed(
            items = tasks,
            key = { _, task -> task.id }
        ) { index, item ->

            val isLast by remember(index) {
                derivedStateOf { index == tasks.lastIndex }
            }

            val selected by remember(selectedTasks) {
                derivedStateOf { selectedTasks.contains(item.id) }
            }

            PriorityItem(
                task = item,
                selected = selected,
                onClick = { onItemClick(item.id) },
                onLongClick = { onItemLongClick(item.id) }
            )

            Row(
                modifier = Modifier.fillParentMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isLast) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = MaterialTheme.colorScheme.divider
                    )
                }
            }
        }
    }
}
