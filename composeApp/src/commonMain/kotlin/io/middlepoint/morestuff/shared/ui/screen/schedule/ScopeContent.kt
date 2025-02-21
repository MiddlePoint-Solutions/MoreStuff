package io.middlepoint.morestuff.shared.ui.screen.schedule

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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.ui.components.PriorityItem
import io.middlepoint.morestuff.shared.ui.compose.simpleVerticalScrollbar
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.theme.divider
import io.middlepoint.morestuff.shared.ui.utils.SharedFunctionsHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.koin.compose.koinInject
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
) {

  var reorderList by remember(tasks) { mutableStateOf(tasks) }
  val sharedFunctionsHandler: SharedFunctionsHandler = koinInject()
  val haptic = sharedFunctionsHandler.rememberReorderHapticFeedback()

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
      .debounce(200)
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
      ReorderableItem(reorderableState, key = item.id) { isDragging ->
        PriorityItem(
          task = item,
          selected = selected,
          onClick = { onItemClick(item.id) },
          onLongClick = { onItemLongClick(item.id) },
          enabled = enabled,
          isDragging = isDragging,
          handleModifier = if (selected) Modifier.draggableHandle(true) else Modifier,
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
