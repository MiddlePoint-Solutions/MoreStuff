package co.softov.morestuff.android.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import co.softov.morestuff.android.ui.model.TaskUiModel

@Composable
fun ScopeContent(
    tasks: List<TaskUiModel>,
    onItemClick: (taskId: Long) -> Unit,
    onItemLongClick: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
    ) {
        itemsIndexed(
            items = tasks,
            key = { _, task -> task.id }
        ) { index, item ->

            // TODO: Better move this into the UI mapped model
            val isLast by remember(index) {
                derivedStateOf { index == tasks.lastIndex }
            }

            PriorityItem(
                task = item,
                onClick = { onItemClick(item.id) },
                onLongClick = { onItemLongClick(item.id) }
            )

            Row(
                modifier = Modifier.fillParentMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isLast) {
                    Divider(
                        thickness = Dp.Hairline,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}
