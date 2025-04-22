package io.middlepoint.morestuff.shared.ui.screen.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.components.PriorityItem
import io.middlepoint.morestuff.shared.ui.extension.simpleVerticalScrollbar
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.theme.divider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@Composable
fun ScopeContent(
  tasks: List<TaskUiModel>,
  modifier: Modifier = Modifier,
  listState: LazyListState = rememberLazyListState(),
  selectedTasks: List<Uuid> = listOf(),
  onItemClick: (taskId: Uuid) -> Unit = {},
  onItemLongClick: (taskId: Uuid) -> Unit = {},
  onTaskComplete: (taskId: Uuid) -> Unit = {},
  onReorder: (updatedTasks: List<TaskUiModel>) -> Unit,
  enabled: Boolean = true,
  isReordering: Boolean,
  onToggleReordering: (Boolean) -> Unit,
  onClearSelection: () -> Unit = {},
) {

  var reorderList by remember(tasks) { mutableStateOf(tasks) }

  fun updateList(from: Int, to: Int) {
    val newList = reorderList.toMutableList().apply {
      val movedTask = removeAt(from)
      add(to, movedTask)
    }
    reorderList = newList
    onReorder(newList)
  }

  val listUpdatedChannel = remember { Channel<Unit>(Channel.CONFLATED) }
  val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
    listUpdatedChannel.tryReceive()
    updateList(from.index, to.index)
    listUpdatedChannel.receive()
  }

  LaunchedEffect(reorderList) {
    listUpdatedChannel.trySend(Unit)
  }


  LaunchedEffect(reorderList) {
    if (reorderList != tasks) {
      onReorder(reorderList)
    }
  }

  LaunchedEffect(isReordering) {
    if (!isReordering) {
      onClearSelection()
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
      key = { _, task -> task.id.value }
    ) { index, item ->

      val isLast by remember(index) {
        derivedStateOf { index == tasks.lastIndex }
      }

      val selected by remember(selectedTasks) {
        derivedStateOf { selectedTasks.contains(item.id) }
      }

      val scope = rememberCoroutineScope()
      var isVisible by remember { mutableStateOf(true) }

      AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
          initialOffsetX = { fullWidth -> fullWidth },
          animationSpec = tween(durationMillis = 600)
        ),
        exit = slideOutHorizontally(
          targetOffsetX = { fullWidth -> -fullWidth },
          animationSpec = tween(durationMillis = 600)
        )
      ) {
        ReorderableItem(reorderableState, key = item.id.value) {
          PriorityItem(
            task = item,
            isSelected = selected,
            isReorderModeActive = isReordering,
            onTaskClick = {
              if (isReordering) {
                onItemLongClick(item.id)
              } else {
                onItemClick(item.id)
              }
            },

            onTaskLongPress = {
              if (!isReordering) {
                onToggleReordering(true)
                onItemLongClick(item.id)
              } else {
                onToggleReordering(false)
              }
            },
            handleModifier = if (isReordering) Modifier.draggableHandle(true) else Modifier,

            onTaskComplete = {
              isVisible = false
              scope.launch {
                delay(200)
                onTaskComplete(item.id)
              }
            },
            enabled = enabled,
          )
        }
      }


      Row(
        modifier = Modifier.fillParentMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
          HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.7.dp,
            color = MaterialTheme.colorScheme.outlineVariant
          )

      }
    }
  }

}

