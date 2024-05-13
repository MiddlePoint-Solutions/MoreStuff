package co.softov.morestuff.android.ui.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.SearchTasksUseCase
import co.softov.morestuff.android.ui.model.TaskUiModel
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import co.softov.morestuff.android.ui.share.ShareEvent.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.koin.compose.koinInject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Composable
fun shareModel(
    initialState: ShareState,
    events: Flow<ShareEvent>,
    getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase = koinInject(),
    searchTasksUseCase: SearchTasksUseCase = koinInject(),
    taskUiMapper: TaskUiMapper = koinInject(),
): ShareState {

    var tasks by remember { mutableStateOf(initialState.tasks) }
    var searchResults by remember { mutableStateOf(initialState.searchResults) }
    val searchQueryFlow = remember { MutableStateFlow("") }

    LaunchedEffect(Unit) {
        getActiveTasksFlowUseCase()
            .map(taskUiMapper::map)
            .collect { tasks = it }
    }

    LaunchedEffect(Unit) {
        searchQueryFlow
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { searchTasksUseCase(it, true) }
            .map(taskUiMapper::map)
            .collect { searchResults = it }
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                ClearSearchQuery -> searchQueryFlow.update { "" }
                is UpdateSearchQuery -> searchQueryFlow.update { event.query }
            }
        }
    }

    return ShareState(
        tasks = tasks,
        searchResults = searchResults
    )
}
