package io.middlepoint.morestuff.shared.ui.screen.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CancelActiveScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetScopeActiveTasksFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.ReorderTaskUseCase
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksModels.Data
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksModels.Loading
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
fun scopeTasksModel(
  scopeId: Uuid,
  events: SharedFlow<ScopeTasksEvent>,
  getScopeActiveTasksFlowUseCase: GetScopeActiveTasksFlowUseCase = koinInject(),
  cancelActiveScheduleUseCase: CancelActiveScheduleUseCase = koinInject(),
  reorderTaskUseCase: ReorderTaskUseCase = koinInject(),
  taskMapper: TaskUiMapper = koinInject()
): ScopeTasksModels {
  var tasks: List<TaskUiModel>? by remember { mutableStateOf(null) }

  LaunchedEffect(Unit) {
    getScopeActiveTasksFlowUseCase(scopeId)
      .map(taskMapper::map)
      .collect { taskList ->
        tasks = taskList
      }
  }

  LaunchedEffect(events) {
    events.collect { event ->
      when (event) {
        is ScopeTasksEvent.ReorderTasks -> {
          reorderTaskUseCase(event.updatedTasks)
        }

      }
    }
  }

/*  LaunchedEffect(tasks) {
    val expiredTaskIds = mutableListOf<Long>()

    tasks?.forEach { task ->
      val currentTime = Clock.System.now().epochSeconds
      val scheduleTime = runCatching { Instant.parse(task.scheduleTime).epochSeconds }.getOrNull()

      if (scheduleTime != null) {
        if (scheduleTime > currentTime) {
          val delayTime = scheduleTime - currentTime
          delay(delayTime * 1000)

          val newCurrentTime = Clock.System.now().epochSeconds
          if (newCurrentTime >= scheduleTime) {
            expiredTaskIds.add(task.id)
          }
        } else {
          expiredTaskIds.add(task.id)
        }
      }
    }

    if (expiredTaskIds.isNotEmpty()) {
      cancelActiveScheduleUseCase(expiredTaskIds)
    }
  }*/

  return tasks?.let { Data(it) } ?: Loading
}