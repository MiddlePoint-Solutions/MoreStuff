package co.softov.morestuff.android.ui.home

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.components.HomeTopBar
import co.softov.morestuff.android.ui.components.MoreStuffHomeScaffold
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserInputViewModel
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.model.NotificationState
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.schedule.PriorityContent
import co.softov.morestuff.android.ui.schedule.TaskOptionsDialog
import co.softov.morestuff.android.ui.search.SearchBar
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer
import co.softov.morestuff.android.ui.theme.surfaceContainerElevation
import co.softov.morestuff.android.ui.utils.explode
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {

    val navigation = LocalAppNavigation.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    val model by viewModel.uiModel.collectAsStateWithLifecycle()
    val taskSelectionActive = model.taskSelectionActive
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val defaultContainerColor = MaterialTheme.colorScheme.surfaceContainer
    val scrollContainerColor = surfaceContainerElevation
    val containerColor = remember { mutableStateOf(defaultContainerColor) }

    LaunchedEffect(scrollBehavior, taskSelectionActive) {
        snapshotFlow { Pair(scrollBehavior.state.overlappedFraction, taskSelectionActive) }
            .collect { (fraction, isActive) ->
                when {
                    isActive -> containerColor.value = scrollContainerColor
                    fraction > 0.2f -> containerColor.value = scrollContainerColor
                    else -> containerColor.value = defaultContainerColor
                }
            }
    }

    BackHandler(model.taskSelectionActive) {
        viewModel.clearSelectedTasks()
    }

    MoreStuffHomeScaffold(
        snackbarHostState = snackbarHostState,
        topAppBarScrollBehavior = scrollBehavior,
        topBar = {
            HomeTopBar(
                model = model,
                reviewSelected = { navigation.push(Screen.Review) },
                settingsSelected = { navigation.push(Screen.Settings) },
                searchSelected = { isSearchActive = true },
                scrollBehavior = scrollBehavior,
                clearTaskSelection = viewModel::clearSelectedTasks,
                completeSelectedTasks = viewModel::completeSelectedTasks,
                deleteSelectedTasks = { showDeleteConfirmationDialog = true },
                containerColor = containerColor

            )
        },
        content = {
            HomeContent(
                showTaskChat = { taskId -> navigation.push(Screen.TaskChat(taskId)) },
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    showTaskChat: (taskId: Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    var showTaskInput by remember { mutableStateOf(false) }
    val priorityScrollState = rememberLazyListState()

    var taskOptions by remember { mutableLongStateOf(0) }
    var showTaskCompleteAnimation by remember { mutableLongStateOf(0) }

    val tasks by homeViewModel.tasks.collectAsState()
    val model by homeViewModel.uiModel.collectAsState()

    val showEmptyState by remember(tasks) { derivedStateOf { tasks.isEmpty() } }

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
                        SnackbarResult.Dismissed -> homeViewModel.resetNotification()
                        SnackbarResult.ActionPerformed -> homeViewModel.undoLastCompleted()
                    }
                }
            }

            else -> {}
        }
    }

    if (taskOptions > 0) {
        val sheetState = rememberModalBottomSheetState()
        val dismissDialog = { taskOptions = 0 }
        TaskOptionsDialog(
            sheetState = sheetState,
            dismissDialog = dismissDialog,
            completeTask = {
                showTaskCompleteAnimation = taskOptions
                scope.launch {
                    homeViewModel.completeTask(taskOptions)
                    sheetState.hide()
                    dismissDialog()
                }
            },
            moveToTop = {
                scope.launch {
                    homeViewModel.moveToTop(taskOptions)
                    sheetState.hide()
                    dismissDialog()
                }
            },
            moveToBottom = {
                scope.launch {
                    homeViewModel.moveToBottom(taskOptions)
                    sheetState.hide()
                    dismissDialog()
                }
            }
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        PriorityContent(
            tasks = tasks,
            onItemClick = { taskId ->
                if (model.taskSelectionActive) {
                    homeViewModel.toggleTaskSelection(taskId)
                } else {
                    showTaskChat(taskId)
                }
            },
            onItemLongClick = homeViewModel::toggleTaskSelection,
            showTaskOptions = { taskOptions = it },
            toggleQuickReminder = homeViewModel::toggleQuickReminder,
            listState = priorityScrollState,
            taskSelectionActive = { model.taskSelectionActive },
            modifier = Modifier.padding(bottom = 30.dp),
        )

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
            onClick = {
                showTaskInput = true
            },
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
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
    }

    if (showTaskInput) {
        val taskInputBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        BackHandler(onBack = {
            scope.launch {
                taskInputBottomSheetState.hide()
            }
        })

        TaskInputBottomSheet(
            onDismissRequest = { showTaskInput = false },
            sheetState = taskInputBottomSheetState,
            scrollNowPriority = {
                scope.launch {
                    priorityScrollState.animateScrollToItem(index = 0)
                }
            },
            scrollLaterPriority = {
                scope.launch {
                    priorityScrollState.scrollToItem(index = tasks.size - 1)
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskInputBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    scrollNowPriority: () -> Unit,
    scrollLaterPriority: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberLazyListState()

    val viewModel: UserInputViewModel = koinViewModel()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val priorityModel by viewModel.priorityModel.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val navigation = LocalAppNavigation.current

    val chatActions = remember {
        ChatActions(
            taskChatAction = {
                navigation.push(Screen.TaskChat(it))
                scope.launch {
                    sheetState.hide()
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        content = {
            BoxWithConstraints {
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Messages(
                        messages = messages,
                        actions = chatActions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        scrollState = scrollState,
                    )

                    PriorityInput(
                        model = priorityModel,
                        onNowSelected = viewModel::setNowPriority,
                        onLaterSelected = viewModel::setLaterPriority,
                        onPlanSelected = viewModel::setPlanPriority,
                        onTimeChange = viewModel::updatePlanTime,
                        onDateChange = viewModel::updatePlanDate,
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                    ) {
                        var userInputValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                            mutableStateOf(TextFieldValue(text = viewModel.userInput))
                        }

                        LaunchedEffect(viewModel.userInput) {
                            userInputValue =
                                userInputValue.copy(text = viewModel.userInput)
                        }

                        UserInput(
                            textContent = {
                                UserTextInput(
                                    value = userInputValue,
                                    onValueChange = { userInputValue = it },
                                    focusRequester = focusRequester,
                                    sendAction = { title ->
                                        scope.launch {
                                            viewModel.createNewTask(title)
                                            userInputValue = userInputValue.copy("")
                                            delay(100)
                                            when (priorityModel.priority) {
                                                PriorityUiModel.Later -> scrollLaterPriority()
                                                PriorityUiModel.Now -> scrollNowPriority()
                                                is PriorityUiModel.Plan -> {}
                                            }
                                        }
                                    },
                                    actionsContent = {
                                        VoiceToTextInput(
                                            onUpdateValue = viewModel::updateUserInput
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    )
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
