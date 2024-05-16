package io.middlepoint.morestuff.android.ui.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.android.domain.usecase.task.GetScopeActiveTasksFlowUseCase
import io.middlepoint.morestuff.android.ui.model.TaskUiModel
import io.middlepoint.morestuff.android.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.android.ui.schedule.ScopeTasksModels.*
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
fun scopeTasksModel(
    scopeId: Long,
    getScopeActiveTasksFlowUseCase: GetScopeActiveTasksFlowUseCase = koinInject(),
    taskMapper: TaskUiMapper = koinInject()
): ScopeTasksModels {

    var tasks: List<TaskUiModel>? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        getScopeActiveTasksFlowUseCase(scopeId)
            .map(taskMapper::map)
            .collect { tasks = it }
    }

    return tasks?.let { Data(it) } ?: Loading
}