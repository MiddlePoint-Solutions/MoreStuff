package io.middlepoint.morestuff.shared.ui.screen.schedule

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Modifier
import io.middlepoint.morestuff.shared.ui.components.PriorityItem
import io.middlepoint.morestuff.shared.ui.compose.simpleVerticalScrollbar
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.theme.divider
import kotlinx.coroutines.FlowPreview
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalFoundationApi::class, FlowPreview::class)
@Composable
fun ScopeContent(
  tasks: List<TaskUiModel>,
  modifier: Modifier = Modifier,
  listState: LazyListState = rememberLazyListState(),
  selectedTasks: List<Long> = listOf(),
  onItemClick: (taskId: Long) -> Unit = {},
  onItemLongClick: (taskId: Long) -> Unit = {},
  onReorder: (updatedTasks: List<TaskUiModel>) -> Unit,
  enabled: Boolean = true,
  onTaskComplete: (taskId: Long) -> Unit = {},
  isReordering: Boolean,
  onToggleReordering: (Boolean) -> Unit,
) {

  var reorderList by remember(tasks) { mutableStateOf(tasks) }

  val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
    val newList = reorderList.toMutableList().apply {
      val movedTask = removeAt(from.index)
      add(to.index, movedTask)
    }
    reorderList = newList
    onReorder(newList)
  }

  LaunchedEffect(reorderList) {
    if (reorderList != tasks) {
      onReorder(reorderList)
    }
  }

  LaunchedEffect(selectedTasks) {
    if (selectedTasks.isEmpty()) {
      onToggleReordering(false)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .simpleVerticalScrollbar(listState),
    state = listState,
    userScrollEnabled = enabled
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


      ReorderableItem(reorderableState, key = item.id) {
        PriorityItem(
          task = item,
          selected = selected,
          hasSelection = isReordering,
          onClick = {
            if (isReordering) {
              onItemLongClick(item.id)
            } else {
              onItemClick(item.id)
            }
          },
          onLongClick = {
            onToggleReordering(!isReordering)
            onItemLongClick(item.id)
          },
          enabled = enabled,
          handleModifier = if (isReordering) Modifier.draggableHandle(true) else Modifier,
          onTaskComplete = { onTaskComplete(item.id) },
          modifier = Modifier.animateItem(
            fadeInSpec = spring(stiffness = Spring.StiffnessMedium),
            fadeOutSpec = spring(stiffness = Spring.StiffnessMedium),
            placementSpec = spring(stiffness = Spring.DampingRatioHighBouncy)
          )
        )
      }

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

