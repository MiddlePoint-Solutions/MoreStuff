package co.softov.morestuff.android.ui.home

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ChatContext
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.components.ConfirmDeleteDialog
import co.softov.morestuff.android.ui.components.HomeTopBar
import co.softov.morestuff.android.ui.components.MoreStuffHomeScaffold
import co.softov.morestuff.android.ui.home.HomeEvent.*
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.model.show
import co.softov.morestuff.android.ui.schedule.ScopeContent
import co.softov.morestuff.android.ui.schedule.ScopeTasksModels
import co.softov.morestuff.android.ui.schedule.ScopeTasksPresenter
import co.softov.morestuff.android.ui.search.SearchBar
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainerElevation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homePresenter: HomePresenter = koinViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val navigation = LocalAppNavigation.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchActive by remember { mutableStateOf(false) }
    var showScopeSelection by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val model by homePresenter.models.collectAsState()

    MoreStuffHomeScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            HomeTopBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
                selectedTaskCount = model.selectedTasks.size,
                reviewSelected = { navigation.push(Screen.Review(model.currentScopeId)) },
                settingsSelected = { navigation.push(Screen.Settings) },
                searchAction = { isSearchActive = true },
                clearTaskSelection = { homePresenter.take(ClearTaskSelection) },
                completeSelectedTasks = { homePresenter.take(CompleteSelectedTasks) },
                deleteSelectedTasks = { showDeleteConfirmationDialog = true },
                selectScope = { showScopeSelection = true },
            )
        },
        content = {
            if (model.scopes.isNotEmpty()) {
                HomeContent(
                    model = model,
                    onEvent = homePresenter::take,
                    modifier = Modifier.padding(it),
                )
            }
        },
    )

    val resources = LocalContext.current.resources
    LaunchedEffect(Unit) {
        homePresenter.notifications.collectLatest { notification ->
            notification.show(snackbarHostState, resources).let { result ->
                if (result == SnackbarResult.ActionPerformed) {
                    launch { notification.action() }
                }
            }
        }
    }

    if (showDeleteConfirmationDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteConfirmationDialog = false },
            onConfirm = {
                homePresenter.take(DeleteSelectedTasks)
                showDeleteConfirmationDialog = false
            }
        )
    }

    AnimatedVisibility(
        visible = isSearchActive,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        SearchBar(
            onSearchClose = { isSearchActive = false },
            showTaskChat = { navigation.push(Screen.TaskChat(it)) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showScopeSelection) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ScopeSelectionBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                    showScopeSelection = false
                }
            },
            scopes = model.scopes,
            sheetState = sheetState,
            addSelectedTasksToScope = { homePresenter.take(MoveSelectedTasksToScope(it)) },
            createNewScope = {
                val createScopeScreen = Screen.CreateScope {
                    homePresenter.take(CreateScopeForSelectedTasks(it))
                    navigation.pop()
                }
                navigation.push(createScopeScreen)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    model: HomeState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    var showTaskInput by remember { mutableStateOf(false) }
    val navigation = LocalAppNavigation.current

    val selectedTasks = model.selectedTasks

    val states by rememberUpdatedState(newValue = model.scopes.map { rememberLazyListState() })
    val pagerState = rememberPagerState(pageCount = { model.scopes.size })
    var currentScopePage by remember { mutableIntStateOf(pagerState.currentPage) }
    val scopes by rememberUpdatedState(newValue = model.scopes)

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                if (currentScopePage != page) {
                    launch { states[currentScopePage].animateScrollToItem(0) }
                    currentScopePage = page
                }
                // TODO: should probably just use the page and let the viewmodel handle the scopeid
                onEvent(ScopeSelected(scopes[page].id))
            }
    }

    BackHandler(selectedTasks.isNotEmpty()) {
        onEvent(ClearTaskSelection)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .shadow(1.dp)
                    .padding(bottom = 0.5.dp)
            ) {
                ScopeTabs(
                    currentPage = pagerState.currentPage,
                    scopes = model.scopes,
                    onScopeSelected = { index, _ ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { model.scopes[it].id }
            ) { page ->

                val scope = model.scopes[page]

                val scopeViewModel = koinViewModel<ScopeTasksPresenter>(
                    key = "Scope${scope.id}",
                    parameters = { parametersOf(scope.id) }
                )

                val scopeTasks by scopeViewModel.models.collectAsState()

                when (val tasksModel = scopeTasks) {
                    is ScopeTasksModels.Data -> {
                        if (tasksModel.tasks.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = { showTaskInput = true }) {
                                    Text(
                                        stringResource(R.string.empty_priority_list_cta),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        } else {
                            ScopeContent(
                                tasks = tasksModel.tasks,
                                selectedTasks = selectedTasks,
                                onItemClick = { taskId ->
                                    if (selectedTasks.isNotEmpty()) {
                                        onEvent(ToggleTaskSelection(taskId))
                                    } else {
                                        navigation.push(Screen.TaskChat(taskId))
                                    }
                                },
                                onItemLongClick = { onEvent(ToggleTaskSelection(it)) },
                                listState = states[page],
                            )
                        }
                    }

                    ScopeTasksModels.Loading -> {

                    }
                }
            }
        }

        if (showTaskInput) {
            val taskInputBottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

            val context = remember { ChatContext.Main(model.currentScopeId) }

            BackHandler(onBack = {
                coroutineScope.launch {
                    taskInputBottomSheetState.hide()
                }
            })

            TaskInputBottomSheet(
                onDismissRequest = { showTaskInput = false },
                sheetState = taskInputBottomSheetState,
                context = context,
                onNewTaskCreated = { _, priority ->
                    coroutineScope.launch {
                        when (priority) {
                            PriorityUiModel.Later -> {
                                val itemCount =
                                    states[pagerState.currentPage].layoutInfo.totalItemsCount
                                states[pagerState.currentPage].scrollToItem(itemCount - 1)
                            }

                            PriorityUiModel.Now -> {
                                states[pagerState.currentPage].animateScrollToItem(index = 0)
                            }

                            is PriorityUiModel.Plan -> {}
                        }
                    }
                },
            )
        }

        FloatingActionButton(
            onClick = { showTaskInput = true },
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
fun MainContentPreview() {
    MoreStuffTheme {
        HomeScreen()
    }
}
