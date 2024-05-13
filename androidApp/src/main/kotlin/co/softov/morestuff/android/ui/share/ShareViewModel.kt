package co.softov.morestuff.android.ui.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.SearchTasksUseCase
import co.softov.morestuff.android.ui.main.MainEvent
import co.softov.morestuff.android.ui.main.MainState
import co.softov.morestuff.android.ui.main.mainModel
import co.softov.morestuff.android.ui.model.TaskUiModel
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class ShareViewModel2 : MoleculeViewModel<ShareEvent, ShareState>() {

    override val initialState: ShareState = ShareState()

    @Composable
    override fun models(events: Flow<ShareEvent>): ShareState {
        return shareModel(initialState, events)
    }
}

@Composable
fun shareModel(
    initialState: ShareState,
    events: Flow<ShareEvent>,
    store: AppStore = koinInject()
): ShareState {
    // TODO: Continue

    return initialState
}
data class ShareState(
    val query: String? = null,
    val tasks: List<TaskUiModel> = listOf(),
    val searchResults: List<TaskUiModel> = listOf()
)

sealed class ShareEvent {

}

class ShareViewModel(
    getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    private val searchTasksUseCase: SearchTasksUseCase,
    private val taskUiMapper: TaskUiMapper,
) : NoStateViewModel() {

    var query by mutableStateOf("")
        private set

    val tasks = getActiveTasksFlowUseCase()
        .map { taskUiMapper.map(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )

    private val searchQueryFlow = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults = searchQueryFlow
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { searchTasksUseCase(it, true) }
        .map { taskUiMapper.map(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = listOf()
        )

    fun updateSearchQuery(searchQuery: String) {
        searchQueryFlow.update { searchQuery }
    }

    fun resetSearchQuery() {
        viewModelScope.launch {
            delay(300)
            searchQueryFlow.update { "" }
        }
    }
}
