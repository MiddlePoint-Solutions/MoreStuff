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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ChatContext
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.components.HomeTopBar
import co.softov.morestuff.android.ui.components.MoreStuffHomeScaffold
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.model.show
import co.softov.morestuff.android.ui.schedule.ScopeContent
import co.softov.morestuff.android.ui.schedule.ScopeTasksModels
import co.softov.morestuff.android.ui.schedule.ScopeTasksPresenter
import co.softov.morestuff.android.ui.search.SearchBar
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainerElevation
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    homePresenter: HomePresenter = koinViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val navigation = LocalAppNavigation.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchActive by remember { mutableStateOf(false) }
    var showScopeSelection by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val selectedTasks by viewModel.selectedTasks.collectAsState()
    val scopesModel by homePresenter.models.collectAsState()

    MoreStuffHomeScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            HomeTopBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
                selectedTaskCount = selectedTasks.size,
                reviewSelected = { navigation.push(Screen.Review(scopesModel.currentScopeId)) },
                settingsSelected = { navigation.push(Screen.Settings) },
                searchAction = { isSearchActive = true },
                clearTaskSelection = viewModel::clearSelectedTasks,
                completeSelectedTasks = viewModel::completeSelectedTasks,
                deleteSelectedTasks = { showDeleteConfirmationDialog = true },
                selectScope = { showScopeSelection = true },
            )
        },
        content = {
            if (scopesModel.scopes.isNotEmpty()) {
                HomeContent(
                    scopesModel = scopesModel,
                    onEvent = homePresenter::take,
                    modifier = Modifier.padding(it),
                )
            }
        },
    )

    val resources = LocalContext.current.resources
    LaunchedEffect(Unit) {
        viewModel.notifications.collectLatest { notification ->
            when (notification.show(snackbarHostState, resources)) {
                SnackbarResult.Dismissed -> {}
                SnackbarResult.ActionPerformed -> launch { notification.action() }
            }
        }
    }

    if (showDeleteConfirmationDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteConfirmationDialog = false },
            onConfirm = {
                viewModel.deleteSelectedTasks()
                showDeleteConfirmationDialog = false
                viewModel.clearSelectedTasks()
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
            addSelectedTasksToScope = viewModel::addSelectedTasksToScope,
            scopes = scopesModel.scopes,
            sheetState = sheetState,
            createNewScope = { viewModel.sendEvent(HomeUiEvent.CreateScopeForSelectedTasks(it)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    scopesModel: HomeScopeState,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    var showTaskInput by remember { mutableStateOf(false) }
    val navigation = LocalAppNavigation.current

    val selectedTasks by viewModel.selectedTasks.collectAsState()

    val states by rememberUpdatedState(newValue = scopesModel.scopes.map { rememberLazyListState() })
    val pagerState = rememberPagerState(pageCount = { scopesModel.scopes.size })
    var currentScopePage by remember { mutableIntStateOf(pagerState.currentPage) }
    val scopes by rememberUpdatedState(newValue = scopesModel.scopes)

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                if (currentScopePage != page) {
                    launch { states[currentScopePage].scrollToItem(0) }
                    currentScopePage = page
                }
                // TODO: should probably just use the page and let the viewmodel handle the scopeid
                onEvent(HomeUiEvent.ScopeSelected(scopes[page].id))
            }
    }

    BackHandler(selectedTasks.isNotEmpty()) {
        viewModel.clearSelectedTasks()
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
                    scopes = scopesModel.scopes,
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
                key = { scopesModel.scopes[it].id }
            ) { page ->

                val scope = scopesModel.scopes[page]

                val scopeViewModel = koinViewModel<ScopeTasksPresenter>(
                    key = "Scope${scope.id}",
                    parameters = { parametersOf(scope.id, viewModel.selectedTasks) }
                )

                val scopeTasks by scopeViewModel.models.collectAsState()

                when (val model = scopeTasks) {
                    is ScopeTasksModels.Data -> {
                        if (model.tasks.isEmpty()) {
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
                                tasks = model.tasks,
                                onItemClick = { taskId ->
                                    if (selectedTasks.isNotEmpty()) {
                                        viewModel.toggleTaskSelection(taskId)
                                    } else {
                                        navigation.push(Screen.TaskChat(taskId))
                                    }
                                },
                                onItemLongClick = viewModel::toggleTaskSelection,
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

            val context = remember { ChatContext.Main(scopesModel.currentScopeId) }

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

@Composable
private fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.confirm_delete)) },
        text = {
            Text(
                text = stringResource(R.string.sure_delete_task),
                textAlign = TextAlign.Start
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
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
