package co.softov.morestuff.android.ui.home

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ChatContext
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.components.HomeTopBar
import co.softov.morestuff.android.ui.components.MoreStuffHomeScaffold
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.model.NotificationState
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.schedule.ScopeContent
import co.softov.morestuff.android.ui.schedule.ScopeViewModel
import co.softov.morestuff.android.ui.scope.CreateScopeButton
import co.softov.morestuff.android.ui.scope.ScopeTabs
import co.softov.morestuff.android.ui.search.SearchBar
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer
import co.softov.morestuff.android.ui.theme.surfaceContainerElevation
import co.softov.morestuff.android.ui.utils.explode
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {

    val navigation = LocalAppNavigation.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var showScopeSelection by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmationDialog by rememberSaveable { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val selectedTasks by viewModel.selectedTasks.collectAsState()
    val model by viewModel.uiModel.collectAsState()

    val topBarContainerColor by animateColorAsState(
        targetValue = if (scrollBehavior.state.overlappedFraction > 0.2f || selectedTasks.isNotEmpty()) {
            MaterialTheme.colorScheme.surfaceContainerElevation
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
        label = "TopBar color animation"
    )

    MoreStuffHomeScaffold(
        snackbarHostState = snackbarHostState,
        topAppBarScrollBehavior = scrollBehavior,
        topBar = {
            HomeTopBar(
                containerColor = topBarContainerColor,
                selectedTaskCount = selectedTasks.size,
                reviewSelected = { navigation.push(Screen.Review(model.selectedScopeId)) },
                settingsSelected = { navigation.push(Screen.Settings) },
                searchAction = { isSearchActive = true },
                scrollBehavior = scrollBehavior,
                clearTaskSelection = viewModel::clearSelectedTasks,
                completeSelectedTasks = viewModel::completeSelectedTasks,
                deleteSelectedTasks = { showDeleteConfirmationDialog = true },
                selectScope = { showScopeSelection = true },
                removeSelectedTasksFromScope = viewModel::removeSelectedTaskFromScope
            )
        },
        content = {
            HomeContent(
                scopesContainerColor = topBarContainerColor,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(it),
            )
        },
    )

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
        val scopes by viewModel.scopes.collectAsState()
        val scopeSelectionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ScopeSelectionBottomSheet(
            onDismissRequest = { showScopeSelection = false },
            addSelectedTasksToScope = viewModel::addSelectedTasksToScope,
            scopes = scopes,
            sheetState = scopeSelectionSheetState,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    scopesContainerColor: Color,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    var showTaskInput by remember { mutableStateOf(false) }
    val navigation = LocalAppNavigation.current
    var showTaskCompleteAnimation by remember { mutableLongStateOf(0) }

    val model by viewModel.uiModel.collectAsState()
    val scopes by viewModel.scopes.collectAsState()
    val selectedTasks by viewModel.selectedTasks.collectAsState()

    val states by rememberUpdatedState(newValue = scopes.map { rememberLazyListState() })
    val pagerState = rememberPagerState(pageCount = { scopes.size })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectScope(scopes[pagerState.currentPage].id)
        states[pagerState.currentPage].scrollToItem(0)
    }

    BackHandler(selectedTasks.isNotEmpty()) {
        viewModel.clearSelectedTasks()
    }

    val resources = LocalContext.current.resources
    LaunchedEffect(model.notification) {
        when (model.notification) {
            NotificationState.Complete -> {
                snackbarHostState.showSnackbar(
                    message = resources.getString(R.string.snack_task_completed),
                    actionLabel = resources.getString(R.string.undo),
                    duration = SnackbarDuration.Long
                ).also {
                    when (it) {
                        SnackbarResult.Dismissed -> viewModel.resetNotification()
                        SnackbarResult.ActionPerformed -> viewModel.undoLastCompleted()
                    }
                }
            }

            NotificationState.None -> {}
        }
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
                    scopes = scopes,
                    onScopeSelected = { index, _ ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    containerColor = scopesContainerColor
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondBoundsPageCount = 1,
                key = { scopes[it].id }
            ) {

                val scopeViewModel = koinViewModel<ScopeViewModel>(
                    key = "Scope$it",
                    parameters = {
                        parametersOf(
                            scopes[it].id,
                            viewModel.selectedTasks
                        )
                    }
                )

                val scopeTasks by scopeViewModel.scopeTasks.collectAsState()

                val showEmptyState by remember(scopeTasks.size) {
                    derivedStateOf { scopeTasks.isEmpty() }
                }

                if (showEmptyState) {
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
                        tasks = scopeTasks,
                        onItemClick = { taskId ->
                            if (selectedTasks.isNotEmpty()) {
                                viewModel.toggleTaskSelection(taskId)
                            } else {
                                navigation.push(Screen.TaskChat(taskId))
                            }
                        },
                        onItemLongClick = viewModel::toggleTaskSelection,
                        listState = states[it],
                        modifier = Modifier.padding(bottom = 20.dp),
                    )
                }
            }
        }

        if (showTaskInput) {
            val taskInputBottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

            val context = remember { ChatContext.Main(model.selectedScopeId) }

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

        if (showTaskCompleteAnimation > 0 && model.confettiEnabled) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = explode(),
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        if (activeSystems == 0) showTaskCompleteAnimation = 0
                    }
                }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopeSelectionBottomSheet(
    onDismissRequest: () -> Unit,
    addSelectedTasksToScope: (Long) -> Unit,
    scopes: List<ScopeDomain>,
    sheetState: SheetState,
) {
    val coroutineScope = rememberCoroutineScope()
    val navigation = LocalAppNavigation.current
    ModalBottomSheet(
        content = {
            BoxWithConstraints {
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ScopeList(
                        scopes = scopes,
                        onScopeSelected = addSelectedTasksToScope,
                        modifier = Modifier.height(216.dp),
                        hideSheet = {
                            coroutineScope.launch {
                                sheetState.hide()
                                onDismissRequest()
                            }
                        }
                    )
                    CreateScopeButton(
                        isUserInputActive = false,
                        onButtonClick = {
                            navigation.push(Screen.CreateScope(userInputActiveInitially = true))
                            coroutineScope.launch {
                                sheetState.hide()
                                onDismissRequest()
                            }
                        }
                    )
                }
            }

        },
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    )
}

@Composable
fun ScopeList(
    scopes: List<ScopeDomain>,
    onScopeSelected: (Long) -> Unit,
    modifier: Modifier,
    hideSheet: () -> Unit,
) {
    val scrollState = rememberLazyListState()

    Text(
        text = (stringResource(R.string.add_to_scope)),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp,
        fontWeight = FontWeight(400),
        modifier = Modifier.padding(start = 10.dp)
    )
    LazyColumn(
        state = scrollState
    ) {
        items(
            scopes.size,
            key = { "Scope$it" }
        ) {
            val scope = scopes[it]
            ListItem(
                modifier = Modifier.clickable {
                    onScopeSelected(scope.id)
                    hideSheet()
                },
                headlineContent = {
                    Text(scope.name)
                },
            )
            if (it < scopes.size - 1) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = Dp.Hairline
                )
            }
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
